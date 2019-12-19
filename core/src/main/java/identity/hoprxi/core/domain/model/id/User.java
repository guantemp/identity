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
package identity.hoprxi.core.domain.model.id;

import com.arangodb.entity.DocumentField;
import identity.hoprxi.core.domain.model.DomainRegistry;
import identity.hoprxi.core.domain.servers.PasswordService;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Pattern;

/***
 * <p>
 *     Username is is mailbox, mobile, phone number, chinese characters etc.
 *     <ul>
 *      <li>Using socialized login</li>
 *      <li>id can be QQ, WeChat, Sina, Alibaba, Baidu's ID number,not support chinese</li>
 *      <li>If the password is not set, the six digit password will be generated randomly and reset by SMS (or e-mail) authentication code.</li>
 *     </ul>
 * </p>
 * @author <a href="www.hoprxi.com/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.2 2018-11-13
 */
public class User {
    public static final User ANONYMOUS = new User() {
        @Override
        public boolean isEnable() {
            return true;
        }
    };
    private static final String ANONYMOUS_OF_ID = "anonymous";
    private static final Pattern CELLPHONE_NUMBER_ZH = Pattern.compile("^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$");
    private static final Pattern FIXED_TELEPHONE_ZH = Pattern.compile("^(0\\d{2}-\\d{8}(-\\d{1,4})?)|(0\\d{3}-\\d{7,8}(-\\d{1,4})?)$");
    private static final Pattern EMAIL = Pattern.compile("^([a-z0-9A-Z]+[-|\\\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\\\.)+[a-zA-Z]{2,}$\"");
    @DocumentField(DocumentField.Type.KEY)
    private String id;
    private String username;
    private String password;
    private String telephoneNumber;
    private String email;
    private Enablement enablement;

    /**
     * Only for anonymous users
     */
    private User() {
        this.id = ANONYMOUS_OF_ID;
        this.username = ANONYMOUS_OF_ID;
        this.password = DomainRegistry.hash().encrypt(ANONYMOUS_OF_ID);
        this.telephoneNumber = ANONYMOUS_OF_ID;
        this.enablement = Enablement.FOREVER;
    }

    /**
     * @param username
     * @param password
     * @param telephoneNumber
     * @throws IllegalArgumentException id is <code>NULL</code> or empty
     *                                  password is <code>NULL</code> or weak
     */
    public User(String id, String username, String password, String telephoneNumber) {
        this(id, username, password, telephoneNumber, new Enablement(false, LocalDateTime.now()));
    }


    /**
     * @param id
     * @param username
     * @param password
     * @param telephoneNumber
     * @param enablement
     * @throws IllegalArgumentException if the length of the id is not [3,64) or is reservation
     *                                  If the length of the username is not [4,255)
     *                                  if password is weak
     */
    public User(String id, String username, String password, String telephoneNumber, Enablement enablement) {
        setId(id);
        setUsername(username);
        protectPassword(password);
        setTelephoneNumber(telephoneNumber);
        setEnablement(enablement);
    }

    /**
     * This is for database load convert
     *
     * @param id
     * @param username
     * @param telephoneNumber
     * @param enablement
     */
    private User(String id, String username, String telephoneNumber, Enablement enablement) {
        setId(id);
        setUsername(username);
        setTelephoneNumber(telephoneNumber);
        setEnablement(enablement);
    }

    /**
     * @param currentPassword
     * @param changedPassword
     * @throws IllegalArgumentException if password no change or currentPassword isn't correct
     */
    public void changPassword(String currentPassword, String changedPassword) {
        currentPassword = Objects.requireNonNull(currentPassword, "currentPassword is required");
        if (currentPassword.equals(changedPassword))
            throw new IllegalArgumentException("password not chang.");
        if (!DomainRegistry.hash().check(currentPassword, password))
            throw new IllegalArgumentException("currentPassword not confirmed.");
        protectPassword(changedPassword);
        DomainRegistry.domainEventPublisher().publish(new UserPasswordChanged(id));
    }

    /**
     * @param telephoneNumber
     */
    public void changTelephoneNumber(String telephoneNumber) {
        if (null != telephoneNumber && !this.telephoneNumber.equals(telephoneNumber) &&
                (CELLPHONE_NUMBER_ZH.matcher(telephoneNumber).find() || FIXED_TELEPHONE_ZH.matcher(telephoneNumber).find())) {
            this.telephoneNumber = telephoneNumber;
            DomainRegistry.domainEventPublisher().publish(new UserTelephoneNumberChanged(id, telephoneNumber));
        }
    }

    public void rename(String username) {
        if (!this.username.equals(username)) {
            setUsername(username);
            DomainRegistry.domainEventPublisher().publish(new UserRenamed(id, username));
        }
    }

    /**
     * @param enablement
     */
    public void defineEnablement(Enablement enablement) {
        setEnablement(enablement);
        DomainRegistry.domainEventPublisher().publish(new UserEnablementChanged(id, enablement));
    }

    public String username() {
        return username;
    }

    public String telephoneNumber() {
        return telephoneNumber;
    }

    public String id() {
        return id;
    }

    public Enablement enablement() {
        return enablement;
    }

    public boolean isEnable() {
        return enablement.isEnable();
    }

    public Creator toCreator() {
        return new Creator(id, username);
    }

    public UserDescriptor toUserDescriptor() {
        return new UserDescriptor(id, username, enablement.expirationDate());
    }

    public GroupMember toGroupMember() {
        return new GroupMember(GroupMemberType.USER, id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id != null ? id.equals(user.id) : user.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("username='" + username + "'")
                .add("password='" + password + "'")
                .add("telephoneNumber='" + telephoneNumber + "'")
                .add("enablement=" + enablement)
                .toString();
    }

    /**
     * @param password
     * @throws IllegalArgumentException if password is weak
     */
    protected void protectPassword(String password) {
        if (new PasswordService().isWeak(password))
            throw new IllegalArgumentException("password is weak.");
        this.password = DomainRegistry.hash().encrypt(password);
    }
    /**
     * @param username
     * @throws IllegalArgumentException If the length of the username is not [1,255)
     */
    protected void setUsername(String username) {
        Objects.requireNonNull(username, "username is required");
        if (username.length() < 1 || username.length() >= 255)
            throw new IllegalArgumentException("username must be 1 to 255 characters");
        this.username = username;
    }

    /**
     * @param id
     * @throws IllegalArgumentException If the length of the id is not [1,64) or is reservation
     */
    private void setId(String id) {
        id = Objects.requireNonNull(id, "id is required").trim();
        if (id.length() < 1 || id.length() > 64)
            throw new IllegalArgumentException("id must be 1 to 64 characters");
        if (ANONYMOUS_OF_ID.equals(id))
            throw new IllegalArgumentException(ANONYMOUS_OF_ID + " is reservation");
        this.id = id;
    }

    protected void setTelephoneNumber(String telephoneNumber) {
        if (null == telephoneNumber)
            telephoneNumber = username;
        if (!CELLPHONE_NUMBER_ZH.matcher(telephoneNumber).find() && !FIXED_TELEPHONE_ZH.matcher(telephoneNumber).find())
            throw new IllegalArgumentException("Illegal telephone number");
        this.telephoneNumber = telephoneNumber;
    }

    protected void setEnablement(Enablement enablement) {
        this.enablement = Objects.requireNonNull(enablement, "enablement is required");
    }

    public String email() {
        return email;
    }

    private void setEmail(String email) {
        this.email = email;
    }
}
