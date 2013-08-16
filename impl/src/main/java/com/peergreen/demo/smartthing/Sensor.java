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

import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.peergreen.demo.smartthing.jpa.DeviceEntity;
import com.peergreen.demo.smartthing.jpa.SensorEntity;
import com.peergreen.demo.smartthing.service.PersistenceService;

@Path("/")
public class Sensor extends AbsRestObject {

    private final String deviceName;
    private final String sensorName;

    public Sensor(PersistenceService persistenceService, String deviceName, String sensorName) {
        super(persistenceService);
        this.deviceName = deviceName;
        this.sensorName = sensorName;
    }

    @Path("channels")
    public Channels getChannels() {
        return new Channels(getPersistenceService(), deviceName, sensorName);
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Response getSensorInfo() {
        SensorEntity sensorEntity = getPersistenceService().findSensor(deviceName, sensorName);
        if (sensorEntity == null) {
            return Response.status(Status.NOT_FOUND).entity("This sensor has not been found").build();
        }
        return Response.status(OK).entity(sensorEntity.toJSon()).build();
    }


    @PUT
    @Consumes(APPLICATION_JSON)
    public Response addSensor(JsonObject jsonObject, @Context UriInfo uriInfo) {
        SensorEntity sensorEntity = getPersistenceService().findSensor(deviceName, sensorName);
        if (sensorEntity != null) {
            return Response.status(Status.CONFLICT).entity("Sensor with name '" + sensorName + "' already exists").build();
        }

        sensorEntity = SensorEntity.from(jsonObject);
        DeviceEntity deviceEntity = getPersistenceService().findDeviceByName(deviceName);
        sensorEntity.setDeviceEntity(deviceEntity);

        getPersistenceService().addSensor(sensorEntity);

        return Response.created(uriInfo.getAbsolutePath()).entity("Sensor with id '" + sensorEntity.getId() + "' has been added").build();
    }


}
