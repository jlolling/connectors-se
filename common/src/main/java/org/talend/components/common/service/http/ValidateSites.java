/*
 * Copyright (C) 2006-2020 Talend Inc. - www.talend.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.talend.components.common.service.http;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

public class ValidateSites {

    private final static boolean CAN_ACCESS_LOCAL = Boolean
            .valueOf(System.getProperty("connectors.enable_local_network_access", "false"));

    private final static List<String> ADDITIONAL_LOCAL_HOSTS = Arrays.asList(new String[] { "224.0.0" // local multicast : from
                                                                                                      // 224.0.0.0 to 224.0.0.255
            , "127.0.0.1", "localhost" });

    private ValidateSites() {
    }

    public static boolean isValidSite(final String base) {
        return isValidSite(base, CAN_ACCESS_LOCAL);
    }

    public static boolean isValidSite(final String base, final boolean can_access_local) {
        if (can_access_local) {
            // we can access all sites
            return true;
        }

        try {
            final URL url = new URL(base);
            final String host = url.getHost();
            final InetAddress inetAddress = Inet4Address.getByName(host);

            return !inetAddress.isSiteLocalAddress()
                    && !ADDITIONAL_LOCAL_HOSTS.stream().filter(h -> host.contains(h)).findFirst().isPresent();
        } catch (MalformedURLException | UnknownHostException e) {
            return false;
        }
    }
}
