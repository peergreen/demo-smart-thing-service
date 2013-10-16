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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.peergreen.demo.weconsole.node.ChannelNode;
import com.peergreen.demo.weconsole.node.DeviceNode;
import com.peergreen.demo.weconsole.node.SensorNode;
import com.vaadin.data.Container;
import com.vaadin.ui.Label;
import com.vaadin.ui.Tree;

/**
 * Task used to refresh the tree.
 * @author Florent Benoit
 */
public class RefreshSubTask {

    private final Container container;

    private final Tree tree;

    private final Client client;

    private final Label labelValue;

    public RefreshSubTask(Container container, Tree tree, Label labelValue) {
        this.container = container;
        this.tree = tree;
        this.labelValue = labelValue;
        this.client = ClientBuilder.newClient();
    }


    protected URI buildURI(String path) {
        return UriBuilder.fromUri("http://10.200.26.238/devices/").path(path).build();
        //return UriBuilder.fromUri("http://localhost:9000/smart-thing-service/devices/").path(path).build();
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


    protected void updateOnlyCell() {
        // print current value of the selected item
        Object object = tree.getValue();
        if (object != null && object instanceof ChannelNode) {
            ChannelNode channelNode = (ChannelNode) object;
            final String val = getValue(channelNode);
            if (val == null) {

                tree.getUI().access(new Runnable() {

                    @Override
                    public void run() {
                        labelValue.setValue("");
                    }
                });


            } else {
                tree.getUI().access(new Runnable() {

                    @Override
                    public void run() {
                        labelValue.setValue(val);
                    }
                });
            }
            System.out.println("found channel and updating it to value =" + val);

        }


    }


    protected String getValue(ChannelNode channelNode) {
        SensorNode sensorNode = channelNode.getSensorNode();
        DeviceNode deviceNode = sensorNode.getDeviceNode();


        URI getChannelValueURI = buildURI(cleanupUri(deviceNode.getDeviceInfo().getUri()).concat("/sensors/").concat(sensorNode.getSensorInfo().getName()).concat("/channels/").concat(String.valueOf(channelNode.getChannelInfo().getId())).concat("/lastValue"));
        Response response = client.target(getChannelValueURI).request().get();
        // Should be equals to the data sent before
        String lastValue = response.readEntity(String.class);

        getChannelValueURI = buildURI(cleanupUri(deviceNode.getDeviceInfo().getUri()).concat("/sensors/").concat(sensorNode.getSensorInfo().getName()).concat("/channels/").concat(String.valueOf(channelNode.getChannelInfo().getId())).concat("/lastServerID"));
        response = client.target(getChannelValueURI).request().get();
        // Should be equals to the data sent before
        String serverId = response.readEntity(String.class);

        getChannelValueURI = buildURI(cleanupUri(deviceNode.getDeviceInfo().getUri()).concat("/sensors/").concat(sensorNode.getSensorInfo().getName()).concat("/channels/").concat(String.valueOf(channelNode.getChannelInfo().getId())).concat("/lastUpdated"));
        response = client.target(getChannelValueURI).request().get();
        // Should be equals to the data sent before
        Long lastUpdated = response.readEntity(Long.class);

        String value = lastValue.concat(" collected from ").concat(serverId).concat(" at ").concat(new Date(lastUpdated).toString());


        return value;

    }



    public void refreshValue() {
        tree.getValue();
    }

}
