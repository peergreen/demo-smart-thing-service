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

import com.peergreen.demo.smartthing.jpa.ChannelEntity;
import com.peergreen.demo.smartthing.jpa.DeviceEntity;
import com.peergreen.demo.smartthing.jpa.SensorEntity;

public interface PersistenceService {

    Collection<DeviceEntity> listDevices();

    DeviceEntity findDeviceByName(String name);

    void addDevice(DeviceEntity deviceEntity);
    void addSensor(SensorEntity sensorEntity);
    void addChannel(ChannelEntity channelEntity);
    void updateChannel(ChannelEntity channelEntity);

    SensorEntity findSensor(String deviceName, String sensorName);

    ChannelEntity findChannel(String deviceName, String sensorName, long id);

}
