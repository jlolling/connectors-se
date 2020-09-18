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
package org.talend.components.assertion.sink;

import lombok.extern.slf4j.Slf4j;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.components.assertion.conf.Config;
import org.talend.components.assertion.service.Service;

import java.io.Serializable;

import org.talend.sdk.component.api.processor.AfterGroup;
import org.talend.sdk.component.api.processor.BeforeGroup;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Processor;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.configuration.Option;

@Slf4j
@Version(1)
@Icon(Icon.IconType.STAR)
@Processor(name = "assert")
@Documentation("")
public class Assertion implements Serializable {

    private final Config config;

    private Service service;

    private transient boolean done = false;

    public Assertion(@Option("configuration") final Config config, final Service service) {
        this.service = service;
        this.config = config;
    }

    @ElementListener
    public void doAssert(final Record in) {
    }

}
