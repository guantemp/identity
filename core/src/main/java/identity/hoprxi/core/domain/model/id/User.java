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
package identity.hoprxi.core.domain.model.id;

import com.arangodb.entity.DocumentField;
import identity.hoprxi.core.domain.model.DomainRegistry;
import identity.hoprxi.core.domain.servers.PasswordService;

import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.regex.Pattern;

/***
 * <p>
 *     Username is email, telephone number, Chinese characters, etc.
 *     <ul>
 *      <li>socialized support</li>
 *      <li>It can be qq,wechat,sina,alibaba,baidu,git,etc. through binding socialization unionId</li>
 *      <li>If the password is not set, the six digit code will be generated randomly and reset by SMS (or email) authentication code.</li>
 *     </ul>
 * </p>
 * @author <a href="www.hoprxi.com/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.3 2020-12-13
 */
public class User {
    public static final User ANONYMOUS = new User() {
        @Override
        public void changPassword(String currentPassword, String newPassword) {
            //do nothing
        }

        @Override
        public void changTelephoneNumber(String telephoneNumber) {
            //do nothing
        }

        @Override
        public void rename(String username) {
            //do nothing
        }

        @Override
        public void defineEnablement(Enablement enablement) {
            //do nothing
        }
    };
    private static final String ANONYMOUS_OF_ID = "anonymous";
    private static final Pattern MOBILE_CN_PATTERN = Pattern.compile("^[1](([3][0-9])|([4][5,7,9])|([5][^4,6,9])|([6][6])|([7][3,5,6,7,8])|([8][0-9])|([9][8,9]))[0-9]{8}$");
    private static final Pattern FIXED_TELEPHONE_CN_PATTERN = Pattern.compile("^(0\\d{2}-\\d{8}(-\\d{1,4})?)|(0\\d{3}-\\d{7,8}(-\\d{1,4})?)$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$");
    @DocumentField(DocumentField.Type.KEY)
    private String id;
    private String username;
    private String password;
    private String telephoneNumber;
    private String email;
    private Enablement enablement;

    /**
     * Only for anonymous user
     */
    private User() {
        this.id = ANONYMOUS_OF_ID;
        this.username = ANONYMOUS_OF_ID;
        this.password = DomainRegistry.hashService().hash(ANONYMOUS_OF_ID);
        this.telephoneNumber = ANONYMOUS_OF_ID;
        this.email = "guanxianghuan@hoprxi.com";
        this.enablement = Enablement.PERMANENCE;
    }

    /**
     * This is for database load convert,don't required rehash password
     *
     * @param id
     * @param username
     * @param telephoneNumber
     * @param email
     * @param enablement
     */
    private User(String id, String username, String telephoneNumber, String email, Enablement enablement) {
        setId(id);
        setUsername(username);
        setTelephoneNumber(telephoneNumber);
        setEmail(email);
        setEnablement(enablement);
    }

    /**
     * @param id
     * @param username
     * @param password
     * @param telephoneNumber
     * @param email
     * @param enablement
     * @throws IllegalArgumentException if the length of the id is not [3,64) or is reservation
     *                                  If the length of the username is not [4,255)
     *                                  if password is weak
     */
    public User(String id, String username, String password, String telephoneNumber, String email, Enablement enablement) {
        setId(id);
        setUsername(username);
        protectPassword(password);
        setTelephoneNumber(telephoneNumber);
        setEmail(email);
        setEnablement(enablement);
    }

    public User(String id, String username, String password) {
        this(id, username, password, null, null, Enablement.PERMANENCE);
    }


    /**
     * @param currentPassword
     * @param newPassword
     * @throws IllegalArgumentException if password no change or currentPassword isn't correct
     */
    public void changPassword(String currentPassword, String newPassword) {
        currentPassword = Objects.requireNonNull(currentPassword, "currentPassword is required");
        if (currentPassword.equals(newPassword))
            throw new IllegalArgumentException("password not chang.");
        if (!DomainRegistry.hashService().check(currentPassword, password))
            throw new IllegalArgumentException("currentPassword not confirmed.");
        protectPassword(newPassword);
        DomainRegistry.domainEventPublisher().publish(new UserPasswordChanged(id));
    }

    /**
     * @param telephoneNumber
     */
    public void changTelephoneNumber(String telephoneNumber) {
        if (null != telephoneNumber && !this.telephoneNumber.equals(telephoneNumber) &&
                (MOBILE_CN_PATTERN.matcher(telephoneNumber).find() || FIXED_TELEPHONE_CN_PATTERN.matcher(telephoneNumber).find())) {
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

    /**
     * @param username
     * @throws IllegalArgumentException If the length of the username is not [1,255)
     */
    private void setUsername(String username) {
        Objects.requireNonNull(username, "username is required");
        if (username.length() < 1 || username.length() >= 255)
            throw new IllegalArgumentException("username must be 1 to 255 characters");
        this.username = username;
    }

    /**
     * @param password
     * @throws IllegalArgumentException if password is weak
     */
    private void protectPassword(String password) {
        if (new PasswordService().isWeak(password))
            throw new IllegalArgumentException("password is weak.");
        this.password = DomainRegistry.hashService().hash(password);
    }


    public String telephoneNumber() {
        return telephoneNumber;
    }

    private void setTelephoneNumber(String telephoneNumber) {
        if (null != telephoneNumber && !MOBILE_CN_PATTERN.matcher(telephoneNumber).find() && !FIXED_TELEPHONE_CN_PATTERN.matcher(telephoneNumber).find())
            throw new IllegalArgumentException("Illegal telephone number");
        if (null == telephoneNumber && MOBILE_CN_PATTERN.matcher(username).find() && FIXED_TELEPHONE_CN_PATTERN.matcher(username).find())
            telephoneNumber = username;
        this.telephoneNumber = telephoneNumber;
    }

    public String id() {
        return id;
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

    public String email() {
        return email;
    }

    private void setEmail(String email) {
        if (email != null && !EMAIL_PATTERN.matcher(email).matches())
            throw new IllegalArgumentException("Illegal email");
        if (null == email && EMAIL_PATTERN.matcher(username).find())
            email = username;
        this.email = email;
    }

    public Enablement enablement() {
        return enablement;
    }

    private void setEnablement(Enablement enablement) {
        this.enablement = Optional.ofNullable(enablement).map(e -> enablement).orElse(Enablement.PERMANENCE);
    }

    public boolean isEnable() {
        return enablement.isEnable() && enablement.isExpired();
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
                .add("telephoneNumber='" + telephoneNumber + "'")
                .add("email='" + email + "'")
                .add("enablement=" + enablement)
                .toString();
    }

/*
    private void setPaymentPassword(String paymentPassword) {
        paymentPassword = Objects.requireNonNull(paymentPassword, "paymentPassword is required").trim();
        if (!paymentPassword.isEmpty()) {
            Matcher matcher = PAYMENT_PASSWORD_PATTERN.matcher(paymentPassword);
            if (!matcher.matches())
                throw new IllegalArgumentException("paymentPassword must 6 digit number");
        }
        HashService hashService = DomainRegistry.getHashService();
        this.paymentPassword = hashService.hash(paymentPassword);
    }
 */
}
