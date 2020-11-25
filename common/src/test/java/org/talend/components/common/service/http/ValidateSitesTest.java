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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class ValidateSitesTest {

    @ParameterizedTest
    @CsvSource(value = { //
            // Forbidden address if connectors.enable_local_network_access == true
            // loopback address
            "http://127.0.0.1/my/api, false, false", //
            "http://127.0.0.1/my/api, true, true", //
            "http://127.0.0.1:80/my/api, false, false", //
            "http://127.0.0.1:80/my/api, true, true", //
            "http://localhost/my/api, false, false", //
            "http://localhost/my/api, true, true", //
            "http://myapp.localhost.com:80/my/api, false, false", //
            "http://myapp.localhost.com:80/my/api, true, true", //
            // RFC1918 / Private Address Space : https://tools.ietf.org/html/rfc1918
            "http://10.0.0.0/my/api, false, false", //
            "http://10.0.0.0/my/api, true, true", //
            "http://10.10.0.0/my/api, false, false", //
            "http://10.10.0.0/my/api, true, true", //
            "http://10.10.10.0/my/api, false, false", //
            "http://10.10.10.0/my/api, true, true", //
            "http://10.10.10.10/my/api, false, false", //
            "http://10.10.10.10/my/api, true, true", //
            "http://10.255.255.255/my/api, false, false", //
            "http://10.255.255.255/my/api, true, true", //
            "http://172.16.0.0/my/api, false, false", //
            "http://172.16.0.0/my/api, true, true", //
            "http://172.16.255.0/my/api, false, false", //
            "http://172.16.255.0/my/api, true, true", //
            "http://172.25.25.25/my/api, false, false", //
            "http://172.25.25.25/my/api, true, true", //
            "http://172.31.0.0/my/api, false, false", //
            "http://172.31.0.0/my/api, true, true", //
            "http://172.31.0.255/my/api, false, false", //
            "http://172.31.0.255/my/api, true, true", //
            "http://192.168.0.0/my/api, false, false", //
            "http://192.168.0.0/my/api, true, true", //
            "http://192.168.128.10/my/api, false, false", //
            "http://192.168.128.10/my/api, true, true", //
            "http://192.168.255.255/my/api, false, false", //
            "http://192.168.255.255/my/api, true, true", //
            // Address found by pentesters
            "https://172.18.0.7/my/api, false, false", //
            "https://172.18.0.7/my/api, true, true", //
            // Multicast / Local subnetwork : https://en.wikipedia.org/wiki/Multicast_address
            "http://224.0.0.128:80/my/api, false, false", //
            "http://224.0.0.128:80/my/api, true, true", //
            "http://224.0.0.1:80/my/api, false, false", //
            "http://224.0.0.1:80/my/api, true, true", //
            "http://224.0.0.255:80/my/api, false, false", //
            "http://224.0.0.255:80/my/api, true, true", //
            //
            // Authorised addresses even if connectors.enable_local_network_access == true (external sites)
            "http://233.0.0.128:80/my/api, false, true", //
            "http://233.0.0.128:80/my/api, true, true", //
            "http://233.0.0.1:80/my/api, false, true", //
            "http://233.0.0.1:80/my/api, true, true", //
            "http://233.0.0.255:80/my/api, false, true", //
            "http://233.0.0.255:80/my/api, true, true", //
            "http://www.external.com/my/api, false, true", //
            "http://www.external.com/my/api, true, true" //
    })
    void valideSite(final String url, final boolean canAccessLocal, final boolean expected) {
        final boolean validSite = ValidateSites.isValidSite(url, canAccessLocal);
        assertEquals(expected, validSite);
    }

}