/*
 *  Copyright 2018 www.foxtail.cc All rights Reserved.
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
package identity.foxtail.core.domain.model;


import event.foxtail.alpha.domain.model.DomainEventPublisher;
import event.foxtail.alpha.infrastruture.simple.SimpleDomainEventPublisher;
import mi.foxtail.crypto.EncryptionService;
import mi.foxtail.crypto.ScryptEncryption;

/***
 * @author <a href="www.foxtail.cc/author/guan xianghuang">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.2 20180122
 */
public class DomainRegistry {
    public static DomainEventPublisher domainEventPublisher() {
        return SimpleDomainEventPublisher.instance();
    }

    public static EncryptionService encryption() {
        return new ScryptEncryption();
    }
}
