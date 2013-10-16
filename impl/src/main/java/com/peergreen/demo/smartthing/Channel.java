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

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.peergreen.demo.smartthing.jpa.ChannelEntity;
import com.peergreen.demo.smartthing.jpa.SensorEntity;
import com.peergreen.demo.smartthing.service.PersistenceService;

public class Channel extends AbsRestObject {

    private final String deviceName;
    private final String sensorName;
    private final long id;

    public Channel(PersistenceService persistenceService, String deviceName, String sensorName, long id) {
        super(persistenceService);
        this.deviceName = deviceName;
        this.sensorName = sensorName;
        this.id = id;
    }


    @GET
    @Produces(APPLICATION_JSON)
    public Response getChannelInfo() {
        ChannelEntity foundChannel = getPersistenceService().findChannel(deviceName, sensorName, id);
        if (foundChannel == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.status(OK).entity(foundChannel.toJSon()).build();
    }

    @PUT
    @Consumes(APPLICATION_JSON)
    public Response addChannel(JsonObject jsonObject, @Context UriInfo uriInfo) {

        ChannelEntity foundChannel = getPersistenceService().findChannel(deviceName, sensorName, id);
        if (foundChannel != null) {
            return Response.status(Status.CONFLICT).entity("Channel with id '" + id + "' already exists").build();
        }

        // get data
        ChannelEntity channelEntity = ChannelEntity.from(jsonObject);
        channelEntity.setId(Long.valueOf(id));

        SensorEntity sensorEntity = getPersistenceService().findSensor(deviceName, sensorName);
        channelEntity.setSensor(sensorEntity);
        getPersistenceService().addChannel(channelEntity);


        return Response.created(uriInfo.getAbsolutePath()).entity("Channel with ID '" + id + "' has been added").build();
    }


    @GET
    @Path("lastValue")
    public Response getLastValue() {
        ChannelEntity foundChannel = getPersistenceService().findChannel(deviceName, sensorName, id);
        if (foundChannel == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        if (foundChannel.getValues().size() > 0) {
            return Response.status(200).entity(foundChannel.getValues().get(foundChannel.getValues().size() - 1)).build();
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    @GET
    @Path("lastUpdated")
    public Response getLastUpdated() {
        ChannelEntity foundChannel = getPersistenceService().findChannel(deviceName, sensorName, id);
        if (foundChannel == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        if (foundChannel.getValues().size() > 0) {
            return Response.status(200).entity(foundChannel.getLastUpdated().get(foundChannel.getLastUpdated().size() - 1)).build();
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    @GET
    @Path("lastServerID")
    public Response getLastServerId() {
        ChannelEntity foundChannel = getPersistenceService().findChannel(deviceName, sensorName, id);
        if (foundChannel == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        if (foundChannel.getValues().size() > 0) {
            return Response.status(200).entity(foundChannel.getServersId().get(foundChannel.getServersId().size() - 1)).build();
        }
        return Response.status(Status.NOT_FOUND).build();
    }



    @GET
    @Path("add/{value}")
    public Response addValue(@PathParam("value") double value) {
        ChannelEntity foundChannel = getPersistenceService().findChannel(deviceName, sensorName, id);
        if (foundChannel == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        foundChannel.getValues().add(value);
        try {
            foundChannel.getServersId().add(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            foundChannel.getServersId().add("Unknown source");
        }
        foundChannel.getLastUpdated().add(System.currentTimeMillis());
        getPersistenceService().updateChannel(foundChannel);


        return Response.status(200).entity("Adding value '" + value + "' to channel '" + id + "'.").build();
    }


}
