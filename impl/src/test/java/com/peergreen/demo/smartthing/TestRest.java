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

import static com.peergreen.demo.smartthing.json.DeviceInfo.newDevice;
import static com.peergreen.demo.smartthing.json.SensorInfo.newSensor;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.RuntimeDelegate;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.peergreen.demo.smartthing.json.ChannelInfo;
import com.peergreen.demo.smartthing.json.DeviceInfo;
import com.peergreen.demo.smartthing.json.SensorInfo;
import com.peergreen.demo.smartthing.service.InMemoryPersistenceService;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class TestRest {

    private static final int PORT = 9876;

    private URI uri;

    private HttpServer server;

    private Client client;

    private static final String DEVICE_NAME = "device1";

    private static final String SENSOR1_NAME = "sensor1";

    private static final long CHANNEL0_ID = 0;


    protected URI buildURI(String path) {
        return UriBuilder.fromUri("http://localhost/").port(PORT).path(path).build();
    }


    @BeforeClass
    public void init() throws IOException {
        this.uri = UriBuilder.fromUri("http://localhost/").port(PORT).build();

        // Create an HTTP server listening at port 8282
        server = HttpServer.create(new InetSocketAddress(uri.getPort()), 0);
        // Create a handler wrapping the JAX-RS application
        DevicesApplication devicesApplication = new DevicesApplication();
        devicesApplication.setPersistenceService(new InMemoryPersistenceService());
        HttpHandler handler = RuntimeDelegate.getInstance().createEndpoint(devicesApplication, HttpHandler.class);
        // Map JAX-RS handler to the server root
        server.createContext(uri.getPath(), handler);
        // Start the server
        server.start();

        this.client = ClientBuilder.newClient();
    }




    /**
     * No device at startup
     */
    @Test
    public void listDevices() {
        List<DeviceInfo> devices = getAllDevices();
        assertNotNull(devices);
        assertEquals(devices.size(), 0);
    }

    /**
     * Add a device
     */
    @Test(dependsOnMethods="listDevices")
    public void addDevice() {
        DeviceInfo deviceInfo = buildDevice();
        URI deviceURI = buildURI(DEVICE_NAME);
        Response response = client.target(deviceURI).request().put(Entity.entity(deviceInfo.toJSon(), MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(CREATED.getStatusCode()));
        String location = (String) response.getHeaders().getFirst("Location");
        assertTrue(response.readEntity(String.class).contains("added"));
        assertTrue(location.endsWith(DEVICE_NAME));
    }


    /**
     * Check that the device added is the correct one
     */
    @Test(dependsOnMethods="addDevice")
    public void getInfoDevice() {
        DeviceInfo deviceInfo = buildDevice();
        URI deviceURI = buildURI(DEVICE_NAME);
        Response response = client.target(deviceURI).request().get();
        assertThat(response.getStatus(), is(OK.getStatusCode()));
        // Should be equals to the data sent before
        JsonObject jsonObject = response.readEntity(JsonObject.class);
        DeviceInfo foundInfo = DeviceInfo.from(jsonObject);
        assertEquals(foundInfo, deviceInfo);
    }

    /**
     * Now, add a sensor
     */
    @Test(dependsOnMethods="getInfoDevice")
    public void addSensor() {
        SensorInfo sensorInfo = buildSensor(SENSOR1_NAME);
        URI deviceURI = buildURI(DEVICE_NAME.concat("/sensors/").concat(SENSOR1_NAME));
        Response response = client.target(deviceURI).request().put(Entity.entity(sensorInfo.toJSon(), MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(CREATED.getStatusCode()));
        String location = (String) response.getHeaders().getFirst("Location");
        assertTrue(response.readEntity(String.class).contains("added"));
        assertTrue(location.endsWith(SENSOR1_NAME));
    }

    /**
     * Check that the device added is the correct one
     */
    @Test(dependsOnMethods="addSensor")
    public void getInfoSensor() {
        SensorInfo sensorInfo = buildSensor(SENSOR1_NAME);
        URI deviceURI = buildURI(DEVICE_NAME.concat("/sensors/").concat(SENSOR1_NAME));
        Response response = client.target(deviceURI).request().get();
        assertThat(response.getStatus(), is(OK.getStatusCode()));
        // Should be equals to the data sent before
        JsonObject jsonObject = response.readEntity(JsonObject.class);
        SensorInfo foundInfo = SensorInfo.from(jsonObject);
        assertEquals(foundInfo, sensorInfo);
    }


    /**
     * Now, add a channel
     */
    @Test(dependsOnMethods="getInfoSensor")
    public void addChannel() {
        ChannelInfo channelInfo = buildChannel(CHANNEL0_ID);
        URI deviceURI = buildURI(DEVICE_NAME.concat("/sensors/").concat(SENSOR1_NAME).concat("/channels/0"));
        Response response = client.target(deviceURI).request().put(Entity.entity(channelInfo.toJSon(), MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(CREATED.getStatusCode()));
        String location = (String) response.getHeaders().getFirst("Location");
        assertTrue(response.readEntity(String.class).contains("added"));
        assertTrue(location.endsWith(String.valueOf(CHANNEL0_ID)));
    }

    /**
     * Check that the device added is the correct one
     */
    @Test(dependsOnMethods="addChannel")
    public void getInfoChannel() {
        ChannelInfo channelInfo = buildChannel(CHANNEL0_ID);
        URI deviceURI = buildURI(DEVICE_NAME.concat("/sensors/").concat(SENSOR1_NAME).concat("/channels/0"));
        Response response = client.target(deviceURI).request().get();
        assertThat(response.getStatus(), is(OK.getStatusCode()));
        // Should be equals to the data sent before
        JsonObject jsonObject = response.readEntity(JsonObject.class);
        ChannelInfo foundInfo = ChannelInfo.from(jsonObject);
        assertEquals(foundInfo, channelInfo);
    }


    /**
     * One device has been insered
     */
    @Test(dependsOnMethods="addDevice")
    public void listDevicesAfterInsered() {
        List<DeviceInfo> devices = getAllDevices();
        assertNotNull(devices);
        assertEquals(devices.size(), 1);

        DeviceInfo deviceInfo = devices.get(0);
        assertEquals(deviceInfo, buildDevice());
    }

    protected List<DeviceInfo> getAllDevices() {
        URI listDevicesURI = buildURI("");
        Response response = client.target(listDevicesURI).request().get();
        assertThat(response.getStatus(), is(OK.getStatusCode()));
        // Should be equals to the data sent before
        JsonArray jsonArray = response.readEntity(JsonArray.class);
        assertNotNull(jsonArray);

        List<DeviceInfo> devices = new ArrayList<>();
        int size = jsonArray.size();
        for (int i = 0; i < size; i++) {
                JsonObject jsonObject = jsonArray.getJsonObject(i);
                devices.add(DeviceInfo.from(jsonObject));
        }
        return devices;
    }

    protected DeviceInfo buildDevice() {
        // Test create a new Device
        String deviceManufacturer = "pg-manufacturer";
        String deviceModel = "pg-device-model";
        String deviceUri = "coap://[1:2:3]";
        String deviceOwner = "peergreen";
        String deviceOS = "pg-operating-system";
        return newDevice().manufacturer(deviceManufacturer).model(deviceModel).uri(deviceUri).owner(deviceOwner).os(deviceOS);
    }

    protected ChannelInfo buildChannel(long id) {
        return ChannelInfo.newChannel().id(id).unit("celsius");
    }

    protected SensorInfo buildSensor(String name) {
        // Test create a new Device
        String sensorID = "0";
        String sensorUri = "coap://[1:2:3]";
        String sensorName = name;
        String sensorLocation = "myLocation";
        return newSensor().sensorID(sensorID).uri(sensorUri).name(sensorName).location(sensorLocation);
    }

}
