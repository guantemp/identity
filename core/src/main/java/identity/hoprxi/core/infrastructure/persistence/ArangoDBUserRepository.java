/*
 *  Copyright 2018 www.hoprxi.com All rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package identity.hoprxi.core.infrastructure.persistence;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.ArangoGraph;
import com.arangodb.entity.DocumentField;
import com.arangodb.model.VertexUpdateOptions;
import com.arangodb.util.MapBuilder;
import com.arangodb.velocypack.VPackSlice;
import identity.hoprxi.core.domain.model.DomainRegistry;
import identity.hoprxi.core.domain.model.id.Enablement;
import identity.hoprxi.core.domain.model.id.User;
import identity.hoprxi.core.domain.model.id.UserRepository;
import mi.hoprxi.id.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/***
 * @author <a href="www.hoprxi.com/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.3 2018-11-13
 */
public class ArangoDBUserRepository implements UserRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArangoDBUserRepository.class);
    private static final VertexUpdateOptions UPDATE_OPTIONS = new VertexUpdateOptions().keepNull(false);
    private static Field passwordField;
    private static Constructor<User> userConstructor;

    static {
        try {
            passwordField = User.class.getDeclaredField("password");
            passwordField.setAccessible(true);
            userConstructor = User.class.getDeclaredConstructor(String.class, String.class, String.class, Enablement.class);
            userConstructor.setAccessible(true);
        } catch (NoSuchFieldException | NoSuchMethodException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Not find such field or constructor", e);
            }
        }
    }

    private ArangoDatabase identity = ArangoDBUtil.getDatabase();

    @Override
    public long count() {
        final String query = " RETURN LENGTH(user)";
        final ArangoCursor<VPackSlice> cursor = identity.query(query, null, null, VPackSlice.class);
        for (; cursor.hasNext(); ) {
            return cursor.next().getAsLong();
        }
        return 0;
    }

    @Override
    public User[] all(long offset, int limit) {
        User[] users = ArangoDBUtil.calculationCollectionSize(identity, User.class, offset, limit);
        final String query = "FOR v IN user LIMIT @offset,@limit RETURN v";
        final Map<String, Object> bindVars = new MapBuilder().put("offset", offset).put("limit", limit).get();
        final ArangoCursor<VPackSlice> slices = identity.query(query, bindVars, null, VPackSlice.class);
        try {
            for (int i = 0; slices.hasNext(); i++)
                users[i] = rebuild(slices.next());
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Can't rebuild user", e);
        }
        return users;
    }

    @Override
    public String nextIdentity() {
        return new ObjectId().id();
    }

    @Override
    public void remove(String id) {
        ArangoGraph graph = identity.graph("identity");
        graph.vertexCollection("user").deleteVertex(id);
    }

    @Override
    public void save(User user) {
        ArangoGraph graph = identity.graph("identity");
        boolean exists = identity.collection("user").documentExists(user.id());
        if (exists) {
            graph.vertexCollection("user").updateVertex(user.id(), user, UPDATE_OPTIONS);
        } else {
            graph.vertexCollection("user").insertVertex(user);
        }
    }

    /*
        public Collection<User> fromUsername(String username) {
            final String query = "FOR v IN user FILTER v.username like @username RETURN v";
            final Map<String, Object> bindVars = new MapBuilder().put("username", "%" + telephoneNumber + "%").get();
            final ArangoCursor<VPackSlice> cursor = identity.query(query, bindVars, null, VPackSlice.class);
            List<User> list = new ArrayList();
            try {
                for (; cursor.hasNext(); ) {
                    list.add(rebuild(cursor.next()));
                }
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Can't rebuild user", e);
                }
            }
            return list;
        }
    */
    @Override
    public User find(String id) {
        ArangoGraph graph = identity.graph("identity");
        VPackSlice slice = graph.vertexCollection("user").getVertex(id, VPackSlice.class);
        if (slice != null) {
            try {
                return rebuild(slice);
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Can't rebuild user", e);
                }
            }
        }
        return null;
    }

    @Override
    public User usernameAuthenticCredentials(String username, String password) {
        final String query = "FOR v IN user FILTER v.username == @username RETURN v";
        final Map<String, Object> bindVars = new MapBuilder().put("username", username).get();
        final ArangoCursor<VPackSlice> cursor = identity.query(query, bindVars, VPackSlice.class);
        return checkPassword(password, cursor);
    }

    @Override
    public boolean isUsernameExists(String username) {
        final String query = "FOR v IN user FILTER v.username == @username RETURN v";
        final Map<String, Object> bindVars = new MapBuilder().put("username", username).get();
        final ArangoCursor<VPackSlice> cursor = identity.query(query, bindVars, VPackSlice.class);
        return cursor.hasNext() ? true : false;
    }

    /**
     * @param slice
     * @return
     */
    private User rebuild(VPackSlice slice) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (slice == null)
            return null;
        String id = slice.get(DocumentField.Type.KEY.getSerializeName()).getAsString();
        if (id.equals(User.ANONYMOUS.id()))
            return User.ANONYMOUS;
        boolean enable = slice.get("enablement").get("enable").getAsBoolean();
        LocalDateTime expirationDate = LocalDateTime.parse(slice.get("enablement").get("expirationDate").getAsString(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
        // System.out.println(expirationDate);
        Enablement enablement = new Enablement(enable, expirationDate);
        String username = slice.get("username").getAsString();
        String telephoneNumber = slice.get("telephoneNumber").getAsString();
        String password = slice.get("password").getAsString();
        User user = userConstructor.newInstance(id, username, telephoneNumber, enablement);
        //User user =new User(id,username,password,telephoneNumber,enablement);
        passwordField.set(user, password);
        return user;
    }

    @Override
    public User telephoneNumberAuthenticCredentials(String telephoneNumber, String password) {
        final String query = "FOR v IN user FILTER v.telephoneNumber == @telephoneNumber RETURN v";
        final Map<String, Object> bindVars = new MapBuilder().put("telephoneNumber", telephoneNumber).get();
        final ArangoCursor<VPackSlice> slices = identity.query(query, bindVars, null, VPackSlice.class);
        return checkPassword(password, slices);
    }

    private User checkPassword(String password, ArangoCursor<VPackSlice> slices) {
        if (slices != null && slices.hasNext()) {
            try {
                User user = rebuild(slices.next());
                if (DomainRegistry.hashService().check(password, (String) passwordField.get(user)))
                    return user;
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Can't rebuild user", e);
                }
            }
        }
        return null;
    }

    @Override
    public boolean isTelephoneNumberExists(String telephoneNumber) {
        final String query = "FOR v IN user FILTER v.telephoneNumber == @telephoneNumber RETURN v";
        final Map<String, Object> bindVars = new MapBuilder().put("telephoneNumber", telephoneNumber).get();
        final ArangoCursor<VPackSlice> cursor = identity.query(query, bindVars, null, VPackSlice.class);
        return cursor.hasNext() ? true : false;
    }
}
