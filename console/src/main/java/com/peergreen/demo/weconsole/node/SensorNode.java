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

import com.peergreen.demo.smartthing.json.SensorInfo;

public class SensorNode extends TreeNode {

    private final DeviceNode deviceNode;
    private final SensorInfo sensorInfo;


    public SensorNode(DeviceNode deviceNode, SensorInfo sensorInfo) {
        super();
        setName(sensorInfo.getName());
        setParent(deviceNode);
        this.deviceNode = deviceNode;
        this.sensorInfo = sensorInfo;
    }

    @Override
    public int hashCode() {
        return sensorInfo.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SensorNode) {
            return this.deviceNode.equals(((SensorNode) other).deviceNode) && this.sensorInfo.equals(((SensorNode) other).sensorInfo);
        }
        return false;
    }

    public DeviceNode getDeviceNode() {
        return deviceNode;
    }

    public SensorInfo getSensorInfo() {
        return sensorInfo;
    }



}
