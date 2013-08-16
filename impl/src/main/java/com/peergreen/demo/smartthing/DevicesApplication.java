/**
 * Copyright 2013 Peergreen S.A.S. All rights reserved.
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.peergreen.demo.smartthing;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.peergreen.demo.smartthing.service.InMemoryPersistenceService;
import com.peergreen.demo.smartthing.service.JPAPersistenceService;
import com.peergreen.demo.smartthing.service.PersistenceService;

@ApplicationPath("/devices")
public class DevicesApplication extends Application {

    public static EntityManager entityManager;

    @PostConstruct
    protected void postConstruct() {
        System.out.println("postConstruct");
    }

    private PersistenceService persistenceService;

    public PersistenceService getPersistenceService() {
        return persistenceService;
    }

    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    @Override
    public Set<Object> getSingletons() {
        Set<Object> set = new HashSet<>();
        if (entityManager != null) {
            persistenceService = new JPAPersistenceService(entityManager);
        }
        if (persistenceService == null) {
            this.persistenceService = new InMemoryPersistenceService();
        }
        set.add(new Devices(persistenceService));
        return set;
    }

}
