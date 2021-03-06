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
package org.talend.components.workday.service;

import org.talend.sdk.component.api.service.http.Configurer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccessTokenConfigurer implements Configurer {

    private static final String CONTENT_TYPE_APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";

    @Override
    public void configure(Connection connection, ConfigurerConfiguration configuration) {
        log.debug("[configure] [{}] {}", connection.getMethod(), connection.getUrl());
        connection //
                .withHeader("Content-Type", CONTENT_TYPE_APPLICATION_X_WWW_FORM_URLENCODED);
    }
}
