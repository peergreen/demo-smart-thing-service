/**
 * Copyright 2013 Peergreen S.A.S. All rights reserved.
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.peergreen.demo.smartthing.jpa;

import static javax.persistence.GenerationType.AUTO;

import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class SensorEntity {

    @Id
    @GeneratedValue(strategy=AUTO)
    private Long idPersistence;

    private Long id;

    private String uri;

    private String name;

    private String location;

    @OneToMany(mappedBy="sensorEntity")
    private List<ChannelEntity> channels;


    @ManyToOne
    private DeviceEntity deviceEntity;

    public SensorEntity() {
        this.channels = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<ChannelEntity> getChannels() {
        return channels;
    }

    public void setChannels(List<ChannelEntity> channels) {
        this.channels = channels;
    }


    public DeviceEntity getDeviceEntity() {
        return deviceEntity;
    }

    public void setDeviceEntity(DeviceEntity deviceEntity) {
        this.deviceEntity = deviceEntity;
    }

    public JsonObject toJSon() {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        write(jsonObjectBuilder, "uri", uri);
        write(jsonObjectBuilder, "sensorID", String.valueOf(id));
        write(jsonObjectBuilder, "name", name);
        write(jsonObjectBuilder, "location", location);
        return jsonObjectBuilder.build();
    }

    protected void write(JsonObjectBuilder jsonObjectBuilder, String key, String value) {
        if (value == null) {
            jsonObjectBuilder.addNull(key);
        } else {
            jsonObjectBuilder.add(key,  value);
        }
    }



    public static SensorEntity from(JsonObject jsonObject) {
        SensorEntity sensorEntity = new SensorEntity();

        JsonValue uri = jsonObject.get("uri");
        if (uri != null && ValueType.STRING == uri.getValueType()) {
            sensorEntity.uri = jsonObject.getString("uri");
        }
        JsonValue sensorID = jsonObject.get("sensorID");
        if (sensorID != null && ValueType.STRING == sensorID.getValueType()) {
            sensorEntity.id = Long.valueOf(jsonObject.getString("sensorID"));
        }
        JsonValue name = jsonObject.get("name");
        if (name != null && ValueType.STRING == name.getValueType()) {
            sensorEntity.name = jsonObject.getString("name");
        }
        JsonValue location = jsonObject.get("location");
        if (location != null && ValueType.STRING == location.getValueType()) {
            sensorEntity.location = jsonObject.getString("location");
        }

        return sensorEntity;

    }

}
