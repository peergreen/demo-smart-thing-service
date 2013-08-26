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
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ChannelEntity {

    @Id
    @GeneratedValue(strategy=AUTO)
    private Long idPersistence;

    private Long id;

    private String unit;

    @Column(name="rawdata")
    @ElementCollection
    private final List<Double> values;

    @ManyToOne
    private SensorEntity sensorEntity;

    public ChannelEntity() {
        this.values = new ArrayList<>();
    }



    public SensorEntity getSensor() {
        return sensorEntity;
    }



    public void setSensor(SensorEntity sensorEntity) {
        this.sensorEntity = sensorEntity;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }

    public List<Double> getValues() {
        return values;
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


    public static ChannelEntity from(JsonObject jsonObject) {
        ChannelEntity channelEntity = new ChannelEntity();

        JsonValue id = jsonObject.get("id");
        if (id != null && ValueType.STRING == id.getValueType()) {
            channelEntity.id = Long.valueOf(jsonObject.getString("id"));
        }
        JsonValue unit = jsonObject.get("unit");
        if (unit != null && ValueType.STRING == unit.getValueType()) {
            channelEntity.unit = jsonObject.getString("unit");
        }
        return channelEntity;

    }

}
