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

import java.util.Collection;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.peergreen.demo.smartthing.jpa.DeviceEntity;
import com.peergreen.demo.smartthing.service.PersistenceService;

@Path("/")
public class Devices extends AbsRestObject {

    public Devices(PersistenceService persistenceService) {
       super(persistenceService);
    }

    @Path("{deviceID}")
    public Device device(@PathParam("deviceID") String deviceID) {
        return new Device(getPersistenceService(), deviceID);
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Response listDevices() {
        Collection<DeviceEntity> devices = getPersistenceService().listDevices();

        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        for (DeviceEntity device : devices) {
            jsonArrayBuilder.add(device.toJSon());
        }
        JsonArray jsonArray = jsonArrayBuilder.build();
        return Response.status(OK).entity(jsonArray).build();

    }
}

