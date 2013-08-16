/**
 * Copyright 2013 Peergreen S.A.S. All rights reserved.
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.peergreen.demo.smartthing.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityExistsException;

import com.peergreen.demo.smartthing.jpa.ChannelEntity;
import com.peergreen.demo.smartthing.jpa.DeviceEntity;
import com.peergreen.demo.smartthing.jpa.SensorEntity;

public class InMemoryPersistenceService implements PersistenceService {

    private final Map<String, DeviceEntity> devices;

    public InMemoryPersistenceService() {
        this.devices = new HashMap<>();
    }

    @Override
    public Collection<DeviceEntity> listDevices() {
        return devices.values();
    }

    @Override
    public DeviceEntity findDeviceByName(String name) {
        return devices.get(name);
    }

    @Override
    public void addDevice(DeviceEntity deviceEntity) {
        if (devices.containsKey(deviceEntity.getName())) {
            throw new EntityExistsException();
        }
        devices.put(deviceEntity.getName(), deviceEntity);
    }

    @Override
    public SensorEntity findSensor(String deviceName, String sensorName) {
        DeviceEntity deviceEntity = findDeviceByName(deviceName);
        if (deviceEntity == null) {
            return null;
        }
        for (SensorEntity sensor : deviceEntity.getSensors()) {
            if (sensorName.equals(sensor.getName())) {
                return sensor;
            }
        }
        return null;
    }

    @Override
    public ChannelEntity findChannel(String deviceName, String sensorName, long id) {
        SensorEntity sensorEntity = findSensor(deviceName, sensorName);
        if (sensorEntity == null) {
            return null;
        }

        for (ChannelEntity channelEntity : sensorEntity.getChannels()) {
            if (id == channelEntity.getId().longValue()) {
                return channelEntity;
            }
        }
        return null;
    }

    @Override
    public void addSensor(SensorEntity sensorEntity) {
        sensorEntity.getDeviceEntity().getSensors().add(sensorEntity);
    }

    @Override
    public void addChannel(ChannelEntity channelEntity) {
        channelEntity.getSensor().getChannels().add(channelEntity);
    }

    @Override
    public void updateChannel(ChannelEntity channelEntity) {

    }

}
