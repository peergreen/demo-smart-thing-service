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
package com.peergreen.demo.weconsole.node;

import com.peergreen.demo.smartthing.json.ChannelInfo;

public class ChannelNode extends TreeNode {

    private final SensorNode sensorNode;
    private final ChannelInfo channelInfo;

    public ChannelNode(SensorNode sensorNode, ChannelInfo channelInfo) {
        super();
        setName("channel-".concat(String.valueOf(channelInfo.getId())));
        setParent(sensorNode);
        this.sensorNode = sensorNode;
        this.channelInfo = channelInfo;
    }

    @Override
    public int hashCode() {
        return channelInfo.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ChannelNode) {
            return this.channelInfo.equals(((ChannelNode) other).channelInfo) && this.sensorNode.equals(((ChannelNode) other).sensorNode);
        }
        return false;
    }

    public SensorNode getSensorNode() {
        return sensorNode;
    }

    public ChannelInfo getChannelInfo() {
        return channelInfo;
    }
}
