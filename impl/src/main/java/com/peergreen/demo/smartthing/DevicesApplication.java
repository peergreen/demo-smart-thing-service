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
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.peergreen.demo.smartthing.service.InMemoryPersistenceService;
import com.peergreen.demo.smartthing.service.JPAPersistenceService;
import com.peergreen.demo.smartthing.service.PersistenceService;

@ApplicationPath("/devices")
public class DevicesApplication extends Application {

    private static EntityManager entityManager;

    public static List<Devices> allDevices = new CopyOnWriteArrayList<>();

    @PostConstruct
    protected void postConstruct() {
        System.out.println("postConstruct with entity manager = " + entityManager);
    }

    private PersistenceService persistenceService;

    public static void setEntityManager(EntityManager entityManager) {
        PersistenceService persistenceService = new JPAPersistenceService(entityManager);
        for (Devices devices : allDevices) {
            System.out.println("Changing persistence service to " + persistenceService);
            devices.setPersistenceService(persistenceService);
        }
    }


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
        System.out.println("Using persistence service ="  + persistenceService);
        Devices devices = new Devices(persistenceService);
        set.add(devices);
        allDevices.add(devices);
        return set;
    }

}
