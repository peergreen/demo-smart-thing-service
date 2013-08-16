/**
 * Copyright 2013 Peergreen S.A.S. All rights reserved.
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.peergreen.demo.smartthing.json;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;


public class DeviceInfo {

    public String getUri() {
        return uri;
    }

    public DeviceInfo uri(String uri) {
        this.uri = uri;
        return this;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public DeviceInfo manufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
        return this;
    }

    public String getOwner() {
        return owner;
    }

    public DeviceInfo owner(String owner) {
        this.owner = owner;
        return this;
    }

    public String getModel() {
        return model;
    }

    public DeviceInfo model(String model) {
        this.model = model;
        return this;
    }

    public String getOs() {
        return os;
    }

    public DeviceInfo os(String os) {
        this.os = os;
        return this;
    }

    private String uri;

    private String manufacturer;

    private String owner;

    private String model;

    private String os;

    @Override
    public int hashCode() {
        return uri.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof DeviceInfo) {
            return this.uri.equals(((DeviceInfo) other).uri);
        }
        return false;
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


    public static DeviceInfo from(JsonObject jsonObject) {
        DeviceInfo deviceInfo = new DeviceInfo();

        JsonValue uri = jsonObject.get("uri");
        if (uri != null && ValueType.STRING == uri.getValueType()) {
            deviceInfo.uri = jsonObject.getString("uri");
        }
        JsonValue model = jsonObject.get("model");
        if (model != null && ValueType.STRING == model.getValueType()) {
            deviceInfo.model = jsonObject.getString("model");
        }
        JsonValue manufacturer = jsonObject.get("manufacturer");
        if (manufacturer != null && ValueType.STRING == manufacturer.getValueType()) {
            deviceInfo.manufacturer = jsonObject.getString("manufacturer");
        }
        JsonValue os = jsonObject.get("os");
        if (os != null && ValueType.STRING == os.getValueType()) {
            deviceInfo.os = jsonObject.getString("os");
        }
        JsonValue owner = jsonObject.get("owner");
        if (owner != null && ValueType.STRING == owner.getValueType()) {
            deviceInfo.owner = jsonObject.getString("owner");
        }

        return deviceInfo;

    }


    public static DeviceInfo newDevice() {
        return new DeviceInfo();
    }

}
