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


import static javax.ws.rs.core.Response.Status.OK;

import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.peergreen.demo.smartthing.jpa.ChannelEntity;
import com.peergreen.demo.smartthing.jpa.SensorEntity;
import com.peergreen.demo.smartthing.service.PersistenceService;

@Path("/")
public class Channels extends AbsRestObject {


    private final String deviceName;
    private final String sensorName;


    public Channels(PersistenceService persistenceService, String deviceName, String sensorName) {
        super(persistenceService);
        this.deviceName = deviceName;
        this.sensorName = sensorName;
    }


    @Path("{channelName}")
    public Channel getChannel(@PathParam("channelName") int id) {
        return new Channel(getPersistenceService(), deviceName, sensorName, id);
    }


    @GET
    public Response list() {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        SensorEntity sensorEntity = getPersistenceService().findSensor(deviceName, sensorName);
        if (sensorEntity == null) {
            return Response.status(Status.NOT_FOUND).build();
        }


        List<ChannelEntity> channels = sensorEntity.getChannels();
        for (ChannelEntity channel : channels) {
            jsonArrayBuilder.add(channel.toJSon());
        }
        JsonArray jsonArray = jsonArrayBuilder.build();
        return Response.status(OK).entity(jsonArray).build();
    }
}

