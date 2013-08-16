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


public class SensorInfo {

    private String uri;

    private String sensorID;

    private String name;

    private String location;

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SensorInfo) {
            return this.name.equals(((SensorInfo) other).name);
        }
        return false;
    }


    public String getUri() {
        return uri;
    }

    public SensorInfo uri(String uri) {
        this.uri = uri;
        return this;
    }

    public String getSensorID() {
        return sensorID;
    }

    public SensorInfo sensorID(String sensorID) {
        this.sensorID = sensorID;
        return this;
    }

    public String getName() {
        return name;
    }

    public SensorInfo name(String name) {
        this.name = name;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public SensorInfo location(String location) {
        this.location = location;
        return this;
    }

    public JsonObject toJSon() {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        write(jsonObjectBuilder, "uri", uri);
        write(jsonObjectBuilder, "sensorID", sensorID);
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


    public static SensorInfo from(JsonObject jsonObject) {
        SensorInfo sensorInfo = new SensorInfo();

        JsonValue uri = jsonObject.get("uri");
        if (uri != null && ValueType.STRING == uri.getValueType()) {
            sensorInfo.uri = jsonObject.getString("uri");
        }
        JsonValue sensorID = jsonObject.get("sensorID");
        if (sensorID != null && ValueType.STRING == sensorID.getValueType()) {
            sensorInfo.sensorID = jsonObject.getString("sensorID");
        }
        JsonValue name = jsonObject.get("name");
        if (name != null && ValueType.STRING == name.getValueType()) {
            sensorInfo.name = jsonObject.getString("name");
        }
        JsonValue location = jsonObject.get("location");
        if (location != null && ValueType.STRING == location.getValueType()) {
            sensorInfo.location = jsonObject.getString("location");
        }

        return sensorInfo;

    }

    public static SensorInfo newSensor() {
        return new SensorInfo();
    }

}
