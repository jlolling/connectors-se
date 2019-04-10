/*
 * Copyright (C) 2006-2019 Talend Inc. - www.talend.com
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

package org.talend.components.couchbase.testutils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.talend.components.couchbase.CouchbaseContainerTest;
import org.talend.components.couchbase.datastore.CouchbaseDataStore;
import org.talend.components.couchbase.service.CouchbaseService;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;
import org.talend.sdk.component.junit5.WithComponents;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WithComponents("org.talend.components.couchbase")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Testing of CouchbaseService class")
public class CouchbaseServiceTest extends CouchbaseContainerTest {

    @Test
    @DisplayName("Test successful connection")
    void couchbaseSuccessfulConnectionTest() {
        CouchbaseDataStore couchbaseDataStore = new CouchbaseDataStore();
        couchbaseDataStore.setBootstrapNodes(COUCHBASE_CONTAINER.getContainerIpAddress());
        couchbaseDataStore.setBucket(BUCKET_NAME);
        couchbaseDataStore.setPassword(BUCKET_PASSWORD);

        CouchbaseService couchbaseService = new CouchbaseService();
        assertEquals(new HealthCheckStatus(HealthCheckStatus.Status.OK, "Connection OK"),
                couchbaseService.healthCheck(couchbaseDataStore));
    }

    @Test
    @DisplayName("Test unsuccessful connection")
    void couchbaseNotSuccessfulConnectionTest() {
        String wrongPassword = "wrongpass";
        String expectedComment = "Passwords for bucket \"student\" do not match.";

        CouchbaseDataStore couchbaseDataStore = new CouchbaseDataStore();
        couchbaseDataStore.setBootstrapNodes(COUCHBASE_CONTAINER.getContainerIpAddress());
        couchbaseDataStore.setBucket(BUCKET_NAME);
        couchbaseDataStore.setPassword(wrongPassword);

        CouchbaseService couchbaseService = new CouchbaseService();
        assertEquals(new HealthCheckStatus(HealthCheckStatus.Status.KO, expectedComment),
                couchbaseService.healthCheck(couchbaseDataStore));
    }

    @Test
    @DisplayName("Two bootstrap nodes without spaces")
    void resolveAddressesTest() {
        String inputUrl = "192.168.0.1,192.168.0.2";
        String[] resultArrayWithUrls = CouchbaseService.resolveAddresses(inputUrl);
        assertEquals("192.168.0.1", resultArrayWithUrls[0], "first expected node");
        assertEquals("192.168.0.2", resultArrayWithUrls[1], "second expected node");
    }

    @Test
    @DisplayName("Two bootstrap nodes with extra spaces")
    void resolveAddressesWithSpacesTest() {
        String inputUrl = " 192.168.0.1,  192.168.0.2";
        String[] resultArrayWithUrls = CouchbaseService.resolveAddresses(inputUrl);
        assertEquals("192.168.0.1", resultArrayWithUrls[0], "first expected node");
        assertEquals("192.168.0.2", resultArrayWithUrls[1], "second expected node");
    }
}
