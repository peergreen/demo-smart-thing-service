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

import java.net.URI;
import java.net.URISyntaxException;

import com.peergreen.demo.smartthing.json.DeviceInfo;

public class DeviceNode extends TreeNode {

    private final DeviceInfo deviceInfo;



    public DeviceNode(DeviceInfo deviceInfo) {
        super();
        this.deviceInfo = deviceInfo;
        setName(cleanupUri(deviceInfo.getUri()));
    }

    @Override
    public int hashCode() {
        return deviceInfo.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof DeviceNode) {
            return this.deviceInfo.equals(((DeviceNode) other).deviceInfo);
        }
        return false;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
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

}
