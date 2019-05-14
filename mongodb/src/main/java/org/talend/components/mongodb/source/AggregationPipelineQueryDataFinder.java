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

package org.talend.components.mongodb.source;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class AggregationPipelineQueryDataFinder implements QueryDataFinder<Document> {

    @Override
    public MongoCursor<Document> findData(MongoCollection<Document> collection, MongoDBInputMapperConfiguration configuration) {
        List<Document> aggregationStages = new ArrayList<>();
        if (configuration.getConfigurationExtension().getAggregationStages() != null) {
            configuration.getConfigurationExtension().getAggregationStages().stream()
                    .forEach(stage -> aggregationStages.add(Document.parse(stage.getStage())));
        }
        MongoCursor<Document> cursor_tMongoDBInput_1 = collection.aggregate(aggregationStages)
                .allowDiskUse(configuration.getConfigurationExtension().isExternalSort()).iterator();
        return cursor_tMongoDBInput_1;
    }
}