/**
 * Copyright 2013 Peergreen S.A.S.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.peergreen.demo.weconsole;

import static com.peergreen.demo.weconsole.MyVaadinUI.ENTRY_NAME;
import static com.peergreen.demo.weconsole.MyVaadinUI.ICON;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.peergreen.demo.smartthing.json.ChannelInfo;
import com.peergreen.demo.smartthing.json.DeviceInfo;
import com.peergreen.demo.smartthing.json.SensorInfo;
import com.peergreen.demo.weconsole.node.ChannelNode;
import com.peergreen.demo.weconsole.node.DeviceNode;
import com.peergreen.demo.weconsole.node.SensorNode;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.server.ClassResource;
import com.vaadin.ui.Label;
import com.vaadin.ui.Tree;

/**
 * Task used to refresh the tree.
 * @author Florent Benoit
 */
public class RefreshTask implements Runnable {

    private final Container container;

    private final Tree tree;

    private final Client client;

    private final Label labelValue;

    public RefreshTask(Container container, Tree tree, Label labelValue) {
        this.container = container;
        this.tree = tree;
        this.labelValue = labelValue;
        this.client = ClientBuilder.newClient();
    }


    protected URI buildURI(String path) {
        return UriBuilder.fromUri("http://localhost:9000/smart-thing-service-impl-1.0.0-SNAPSHOT/devices/").path(path).build();
    }

   protected String cleanupUri(String input) {
       URI uri;
       try {
           uri = new URI(input);
       } catch (URISyntaxException e) {
           return input;
       }
       return uri.getHost().replace("[", "").replace("]", "");
   }

    @Override
    public void run() {
        updateTree();
    }


    protected void updateTree() {
        // get all devices
        List<DeviceInfo> devices = getAllDevices();

        // First, create devices
        for (DeviceInfo device : devices) {
            DeviceNode deviceNode = new DeviceNode(device);
            // Not/Found
            if (container.getItem(deviceNode) == null) {
                Item item = container.addItem(deviceNode);
                item.getItemProperty(ENTRY_NAME).setValue(deviceNode.getName());
                item.getItemProperty(ICON).setValue(new ClassResource(RefreshTask.class, "/device.png"));
                tree.setChildrenAllowed(deviceNode, true);
            }

            // Update device by adding sensors
            updateDevice(deviceNode);

        }

        // print current value of the selected item
        Object object = tree.getValue();
        if (object != null && object instanceof ChannelNode) {
            ChannelNode channelNode = (ChannelNode) object;
            String val = getValue(channelNode);
            if (val == null) {
                this.labelValue.setValue("");
            } else {
                this.labelValue.setValue(val);
            }
        }


    }


    protected void updateDevice(DeviceNode deviceNode) {
        // get all sensors
        List<SensorInfo> sensors = getAllSensorsForDevice(deviceNode.getDeviceInfo());


        // First, create sensor if not yet available
        for (SensorInfo sensor : sensors) {
            SensorNode sensorNode = new SensorNode(deviceNode, sensor);
            // Not/Found
            if (container.getItem(sensorNode) == null) {
                Item item = container.addItem(sensorNode);
                item.getItemProperty(ENTRY_NAME).setValue(sensorNode.getName());
                item.getItemProperty(ICON).setValue(new ClassResource(RefreshTask.class, "/sensor.png"));

                tree.setChildrenAllowed(sensorNode, true);
                tree.setParent(sensorNode, sensorNode.getParent());
            }

            // Update device by adding sensors
            updateSensor(deviceNode, sensorNode);

        }
    }


    protected void updateSensor(DeviceNode deviceNode, SensorNode sensorNode) {
        // get all sensors
        List<ChannelInfo> channels = getAllChannelsForSensor(deviceNode.getDeviceInfo(), sensorNode.getSensorInfo());


        // First, create sensor if not yet available
        for (ChannelInfo channel : channels) {
            ChannelNode channelNode = new ChannelNode(sensorNode, channel);
            // Not/Found
            if (container.getItem(channelNode) == null) {
                Item item = container.addItem(channelNode);
                item.getItemProperty(ENTRY_NAME).setValue(channelNode.getName());
                item.getItemProperty(ICON).setValue(new ClassResource(RefreshTask.class, "/channel.png"));
                tree.setChildrenAllowed(channelNode, false);
                tree.setParent(channelNode, channelNode.getParent());
            }
        }
    }

    protected String getValue(ChannelNode channelNode) {
        SensorNode sensorNode = channelNode.getSensorNode();
        DeviceNode deviceNode = sensorNode.getDeviceNode();


        URI getChannelValueURI = buildURI(cleanupUri(deviceNode.getDeviceInfo().getUri()).concat("/sensors/").concat(sensorNode.getSensorInfo().getName()).concat("/channels/").concat(String.valueOf(channelNode.getChannelInfo().getId())).concat("/lastValue"));
        Response response = client.target(getChannelValueURI).request().get();
        // Should be equals to the data sent before
        String value = response.readEntity(String.class);
        return value;

    }


    protected List<DeviceInfo> getAllDevices() {
        URI listDevicesURI = buildURI("");
        Response response = client.target(listDevicesURI).request().get();
        // Should be equals to the data sent before
        JsonArray jsonArray = response.readEntity(JsonArray.class);

        List<DeviceInfo> devices = new ArrayList<DeviceInfo>();
        int size = jsonArray.size();
        for (int i = 0; i < size; i++) {
            JsonObject jsonObject = jsonArray.getJsonObject(i);
            devices.add(DeviceInfo.from(jsonObject));
        }
        return devices;
    }

    protected List<SensorInfo> getAllSensorsForDevice(DeviceInfo device) {
        URI listSensorsURI = buildURI(cleanupUri(device.getUri()).concat("/sensors/"));
        Response response = client.target(listSensorsURI).request().get();
        // Should be equals to the data sent before
        JsonArray jsonArray = response.readEntity(JsonArray.class);

        List<SensorInfo> sensors = new ArrayList<SensorInfo>();
        int size = jsonArray.size();
        for (int i = 0; i < size; i++) {
            JsonObject jsonObject = jsonArray.getJsonObject(i);
            sensors.add(SensorInfo.from(jsonObject));
        }
        return sensors;
    }



    protected List<ChannelInfo> getAllChannelsForSensor(DeviceInfo device, SensorInfo sensor) {
        URI listChannelsURI = buildURI(cleanupUri(device.getUri()).concat("/sensors/").concat(sensor.getName()).concat("/channels/"));
        Response response = client.target(listChannelsURI).request().get();
        // Should be equals to the data sent before
        JsonArray jsonArray = response.readEntity(JsonArray.class);

        List<ChannelInfo> channels = new ArrayList<ChannelInfo>();
        int size = jsonArray.size();
        for (int i = 0; i < size; i++) {
            JsonObject jsonObject = jsonArray.getJsonObject(i);
            channels.add(ChannelInfo.from(jsonObject));
        }
        return channels;
    }


    public void refreshValue() {
        tree.getValue();
    }

}
