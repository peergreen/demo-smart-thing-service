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


public class ChannelInfo {

    private long id;

    private String unit;

    public ChannelInfo id(long id) {
        this.id = id;
        return this;
    }

    public ChannelInfo unit(String unit) {
        this.unit = unit;
        return this;
    }

    public long getId() {
        return id;
    }

    public String getUnit() {
        return unit;
    }


    @Override
    public int hashCode() {
        return Long.valueOf(id).hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ChannelInfo) {
            return this.id == (((ChannelInfo) other).id);
        }
        return false;
    }


    public JsonObject toJSon() {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        write(jsonObjectBuilder, "id", String.valueOf(id));
        write(jsonObjectBuilder, "unit", unit);
        return jsonObjectBuilder.build();
    }

    protected void write(JsonObjectBuilder jsonObjectBuilder, String key, String value) {
        if (value == null) {
            jsonObjectBuilder.addNull(key);
        } else {
            jsonObjectBuilder.add(key,  value);
        }
    }



    public static ChannelInfo from(JsonObject jsonObject) {
        ChannelInfo channelInfo = new ChannelInfo();

        JsonValue id = jsonObject.get("id");
        if (id != null && ValueType.STRING == id.getValueType()) {
            channelInfo.id = Long.valueOf(jsonObject.getString("id"));
        }
        JsonValue unit = jsonObject.get("unit");
        if (unit != null && ValueType.STRING == unit.getValueType()) {
            channelInfo.unit = jsonObject.getString("unit");
        }
        return channelInfo;

    }

    public static ChannelInfo newChannel() {
        return new ChannelInfo();
    }

}
