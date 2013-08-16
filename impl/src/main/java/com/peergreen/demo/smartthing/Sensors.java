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

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;

import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.peergreen.demo.smartthing.jpa.DeviceEntity;
import com.peergreen.demo.smartthing.jpa.SensorEntity;
import com.peergreen.demo.smartthing.service.PersistenceService;


public class Sensors extends AbsRestObject {

    private final String deviceName;

    public Sensors(PersistenceService persistenceService, String deviceName) {
        super(persistenceService);
        this.deviceName = deviceName;
    }

    @Path("{sensorname}")
    public Sensor getSensor(@PathParam("sensorname") String sensorName) {
        return new Sensor(getPersistenceService(), deviceName, sensorName);
    }


    @GET
    @Produces(APPLICATION_JSON)
    public Response listSensors() {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        DeviceEntity deviceEntity = getPersistenceService().findDeviceByName(deviceName);
        if (deviceEntity == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        List<SensorEntity> sensors = deviceEntity.getSensors();
        for (SensorEntity sensor : sensors) {
            jsonArrayBuilder.add(sensor.toJSon());
        }

        JsonArray jsonArray = jsonArrayBuilder.build();
        return Response.status(OK).entity(jsonArray).build();
    }


}

