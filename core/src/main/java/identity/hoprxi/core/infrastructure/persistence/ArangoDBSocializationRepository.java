/*
 * Copyright (c) 2020 www.hoprxi.com All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package identity.hoprxi.core.infrastructure.persistence;

import com.arangodb.ArangoDatabase;
import com.arangodb.model.VertexUpdateOptions;
import identity.hoprxi.core.domain.model.id.Socialization;
import identity.hoprxi.core.domain.model.id.SocializationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

/**
 * @author <a href="www.hoprxi.com/author/guan xianghuang">guan xiangHuan</a>
 * @version 0.0.1 2020-12-15
 * @since JDK8.0
 */
public class ArangoDBSocializationRepository implements SocializationRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArangoDBSocializationRepository.class);
    private static final VertexUpdateOptions UPDATE_OPTIONS = new VertexUpdateOptions().keepNull(false);
    private static Constructor<Socialization> socializationConstructor;

    static {
        try {
            socializationConstructor = Socialization.class.getDeclaredConstructor(String.class, String.class, Socialization.ThirdParty.class);
            socializationConstructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Not find such constructor for Socialization", e);
            }
        }
    }

    private final ArangoDatabase identity;

    public ArangoDBSocializationRepository(String databaseName) {
        identity = ArangoDBUtil.getResource().db(databaseName);
    }

    @Override
    public void save(Socialization socialization) {

    }

    @Override
    public Socialization find(String id) {
        return null;
    }

    @Override
    public void remove(String id) {

    }
}

