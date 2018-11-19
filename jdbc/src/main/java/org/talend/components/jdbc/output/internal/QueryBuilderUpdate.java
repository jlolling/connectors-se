/*
 * Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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
package org.talend.components.jdbc.output.internal;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.talend.components.jdbc.configuration.OutputConfiguration;
import org.talend.components.jdbc.service.I18nMessage;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueryBuilderUpdate implements QueryBuilder {

    private final OutputConfiguration configuration;

    private final I18nMessage i18n;

    private final List<String> keys;

    private final List<String> ignoreColumns;

    private Map<Integer, Schema.Entry> queryParams;

    public QueryBuilderUpdate(final OutputConfiguration configuration, final I18nMessage i18n) {
        this.i18n = i18n;
        this.configuration = configuration;
        this.keys = new ArrayList<>(ofNullable(configuration.getKeys()).orElse(emptyList()));
        if (this.keys.isEmpty()) {
            throw new IllegalArgumentException(i18n.errorNoKeyForUpdateQuery());
        }
        this.ignoreColumns = new ArrayList<>(ofNullable(configuration.getIgnoreUpdate()).orElse(emptyList()));
    }

    @Override
    public boolean validateQueryParam(final Record record) {
        return record.getSchema().getEntries().stream().map(Schema.Entry::getName).collect(toSet())
                .containsAll(new HashSet<>(keys));
    }

    @Override
    public Map<Integer, Schema.Entry> getQueryParams() {
        return queryParams;
    }

    @Override
    public String buildQuery(final List<Record> records) {
        this.queryParams = new HashMap<>();
        final AtomicInteger index = new AtomicInteger(0);
        final List<Schema.Entry> entries = records.stream().flatMap(r -> r.getSchema().getEntries().stream()).distinct()
                .collect(toList());
        final String query = "UPDATE " + configuration.getDataset().getTableName() + " SET "
                + entries.stream().filter(e -> !ignoreColumns.contains(e.getName()) && !keys.contains(e.getName()))
                        .peek(e -> queryParams.put(index.incrementAndGet(), e)).map(c -> c.getName() + " = ?")
                        .collect(joining(","))
                + " WHERE " + keys.stream().map(c -> c + " = ?").collect(joining(" AND "));

        keys.stream()
                .map(key -> entries.stream().filter(e -> key.equals(e.getName())).findFirst()
                        .orElseThrow(() -> new IllegalStateException(i18n.errorNoFieldForQueryParam(key))))
                .forEach(entry -> queryParams.put(index.incrementAndGet(), entry));

        log.debug("[query] : " + query);
        return query;
    }
}
