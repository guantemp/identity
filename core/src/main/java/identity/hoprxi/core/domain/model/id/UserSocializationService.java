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

import identity.hoprxi.core.infrastructure.persistence.ArangoDBSocializationRepository;
import identity.hoprxi.core.infrastructure.persistence.ArangoDBUserRepository;

import java.util.Optional;

/**
 * @author <a href="www.hoprxi.com/author/guan xianghuang">guan xiangHuan</a>
 * @version 0.0.1 2020-12-14
 * @since JDK8.0
 */
public class UserSocializationService {
    private UserRepository userRepository = new ArangoDBUserRepository("identity");
    private SocializationRepository socializationRepository = new ArangoDBSocializationRepository("identity");

    public void createSocialization(String unionId, String userId, String thirdParty) {
        Socialization.ThirdParty from = Socialization.ThirdParty.valueOf(thirdParty);
        //Optional.ofNullable(userRepository.find(userId))
        //         .map(u->new Socialization(unionId,userId,from))
        //       .flatMap()
        //        .orElseThrow(()->new IllegalArgumentException(""));
        User user = userRepository.find(userId);
        if (user == null)
            throw new IllegalArgumentException("user is't exists");
        new Socialization(unionId, userId, from);
    }

    public void bindUser(String userId, String unionId, String thirdPartyName) {
        User user = userRepository.find(userId);
        //Optional.ofNullable(user).orElseGet()
        if (user == null)
            throw new IllegalArgumentException("user is't exists");
        Socialization socialization = new Socialization(unionId, userId, Socialization.ThirdParty.valueOf(thirdPartyName));
        socializationRepository.save(socialization);
    }

    public void unbindUser(String unionId) {
        socializationRepository.remove(unionId);
    }

    public UserDescriptor getBindUser(String unionId) {
        Socialization socialization = socializationRepository.find(unionId);
        return Optional.ofNullable(socialization).map(u -> {
            User user = userRepository.find(socialization.userId());
            return user.toUserDescriptor();
        }).orElseGet(() -> UserDescriptor.NullUserDescriptor);
    }
}
