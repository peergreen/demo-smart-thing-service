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
import javax.persistence.OneToMany;

@Entity
public class DeviceEntity {

    @Id
    @GeneratedValue(strategy=AUTO)
    private Long id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    private String name;

    private String uri;

    private String manufacturer;

    private String owner;

    private String model;

    private String os;

    @OneToMany(mappedBy="deviceEntity")
    private List<SensorEntity> sensors;

    public DeviceEntity() {
        this.sensors = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getOwner() {
        return owner;
    }

    public String getModel() {
        return model;
    }

    public String getOs() {
        return os;
    }


    public List<SensorEntity> getSensors() {
        return sensors;
    }

    public void setSensors(List<SensorEntity> sensors) {
        this.sensors = sensors;
    }


    public static DeviceEntity from(JsonObject jsonObject) {
        DeviceEntity deviceEntity = new DeviceEntity();

        JsonValue uri = jsonObject.get("uri");
        if (uri != null && ValueType.STRING == uri.getValueType()) {
            deviceEntity.uri = jsonObject.getString("uri");
        }
        JsonValue model = jsonObject.get("model");
        if (model != null && ValueType.STRING == model.getValueType()) {
            deviceEntity.model = jsonObject.getString("model");
        }
        JsonValue manufacturer = jsonObject.get("manufacturer");
        if (manufacturer != null && ValueType.STRING == manufacturer.getValueType()) {
            deviceEntity.manufacturer = jsonObject.getString("manufacturer");
        }
        JsonValue os = jsonObject.get("os");
        if (os != null && ValueType.STRING == os.getValueType()) {
            deviceEntity.os = jsonObject.getString("os");
        }
        JsonValue owner = jsonObject.get("owner");
        if (owner != null && ValueType.STRING == owner.getValueType()) {
            deviceEntity.owner = jsonObject.getString("owner");
        }

        return deviceEntity;

    }

    public JsonObject toJSon() {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        write(jsonObjectBuilder, "uri", uri);
        write(jsonObjectBuilder, "model", model);
        write(jsonObjectBuilder, "manufacturer", manufacturer);
        write(jsonObjectBuilder, "os", os);
        write(jsonObjectBuilder, "owner", owner);
        return jsonObjectBuilder.build();
    }


    protected void write(JsonObjectBuilder jsonObjectBuilder, String key, String value) {
        if (value == null) {
            jsonObjectBuilder.addNull(key);
        } else {
            jsonObjectBuilder.add(key,  value);
        }
    }

}
