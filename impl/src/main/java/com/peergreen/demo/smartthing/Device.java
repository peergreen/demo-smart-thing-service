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
import static javax.ws.rs.core.Response.created;
import static javax.ws.rs.core.Response.Status.OK;

import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.peergreen.demo.smartthing.jpa.DeviceEntity;
import com.peergreen.demo.smartthing.json.DeviceInfo;
import com.peergreen.demo.smartthing.service.PersistenceService;


@Path("/")
public class Device extends AbsRestObject {

    private final String name;

    public Device(PersistenceService persistenceService, String name) {
        super(persistenceService);
        this.name = name;
    }

    @Path("sensors")
    public Sensors getSensors() {
        return new Sensors(getPersistenceService(), name);
    }

    @PUT
    @Consumes(APPLICATION_JSON)
    public Response addDevice(JsonObject jsonObject, @Context UriInfo uriInfo) {

        DeviceEntity foundDevice = getPersistenceService().findDeviceByName(name);
        if (foundDevice != null) {
            return Response.status(Status.CONFLICT).entity("Device with name '" + name + "' already exists").build();
        }

        // get data
        DeviceEntity deviceEntity = DeviceEntity.from(jsonObject);
        deviceEntity.setName(name);

        getPersistenceService().addDevice(deviceEntity);
        return Response.created(uriInfo.getAbsolutePath()).entity("Device with name '" + name + "' has been added").build();
    }


    //FIXME
    @POST
    @Consumes(APPLICATION_JSON)
    public Response updateDevice(DeviceInfo deviceInfo, @Context UriInfo uriInfo) {
        return created(uriInfo.getAbsolutePath()).entity("Device with name '" + name + "' has been updated").build();
    }


    @GET
    @Produces(APPLICATION_JSON)
    public Response getDevice() {
        DeviceEntity foundDevice = getPersistenceService().findDeviceByName(name);
        if (foundDevice == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        return Response.status(OK).entity(foundDevice.toJSon()).build();
    }




}