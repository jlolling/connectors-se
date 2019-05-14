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

package org.talend.components.cosmosdb.output;

import org.talend.components.cosmosdb.configuration.CosmosDBDatastore;
import org.talend.components.cosmosdb.configuration.CosmosDBOutputConfiguration;
import org.talend.components.cosmosdb.output.processors.ApiProcessor;
import org.talend.components.cosmosdb.output.processors.MongoApiProcessor;
import org.talend.components.cosmosdb.service.CosmosDBService;
import org.talend.components.mongodb.output.MongoDBOutput;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.AfterGroup;
import org.talend.sdk.component.api.processor.BeforeGroup;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Processor;
import org.talend.sdk.component.api.record.Record;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.Serializable;

@Version(1)
@Icon(value = Icon.IconType.STAR)
@Processor(name = "CosmosDBOutput")
@Documentation("CosmosDB output component")
public class CosmosDBOutput implements Serializable {

    private final CosmosDBOutputConfiguration configuration;

    private final CosmosDBService service;

    private ApiProcessor apiProcessor;

    public CosmosDBOutput(@Option("configuration") final CosmosDBOutputConfiguration configuration,
            final CosmosDBService service) {
        this.configuration = configuration;
        this.service = service;
    }

    @PostConstruct
    public void init() {
        if (configuration.getMongoConfig().getDatastore().getApi() == CosmosDBDatastore.SupportedApi.MONGODB) {
            apiProcessor = new MongoApiProcessor(new MongoDBOutput(service.convertToMongoApiConfig(configuration),
                    service.getMongoDBService(), service.getMongoDBService().getI18nMessage()));
        }
        throw new IllegalArgumentException("Unknown API");
    }

    @BeforeGroup
    public void beforeGroup() {
        apiProcessor.beforeGroup();
    }

    @ElementListener
    public void onNext(Record record) {
        apiProcessor.process(record);
    }

    @AfterGroup
    public void afterGroup() {
        apiProcessor.afterGroup();
    }

    @PreDestroy
    public void release() {
        apiProcessor.close();
    }
}