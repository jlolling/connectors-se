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
package org.talend.components.assertion.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.talend.components.assertion.conf.Config;
import org.talend.components.common.stream.api.RecordIORepository;
import org.talend.components.common.stream.input.json.JsonToRecord;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.RecordPointerFactory;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.api.service.record.RecordService;
import org.talend.sdk.component.junit.BaseComponentsHandler;
import org.talend.sdk.component.junit5.Injected;
import org.talend.sdk.component.junit5.WithComponents;

import javax.json.JsonReaderFactory;
import javax.json.stream.JsonParserFactory;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

@WithComponents("org.talend.components.assertion")
class RecordValidatorTest {

    @Injected
    private BaseComponentsHandler componentsHandler;

    private RecordValidator validator;

    @BeforeEach
    public void beforeEach() {
        validator = new RecordValidator();
    }

    @Test
    public void validate() {
        String expected = "{\"last_name\" : \"Smith\", \"first_name\" : \"Agent\", \"age\" : \"__INT__40\", \"address\" : {\"city\": \"Sydney\", \"country\" : \"Australia\"}, \"birthdate\" : \"__DATETIME__1980/07/19 00:00:00+00\"}";

        final RecordBuilderFactory recordBuilderFactory = componentsHandler.findService(RecordBuilderFactory.class);
        final RecordService recordService = componentsHandler.findService(RecordService.class);
        final JsonReaderFactory jsonReaderFactory = componentsHandler.findService(JsonReaderFactory.class);
        final JsonParserFactory jsonParserFactory = componentsHandler.findService(JsonParserFactory.class);

        validator.setJsonParserFactory(jsonParserFactory);
        validator.setJsonReaderFactory(jsonReaderFactory);
        validator.setRecordBuilderFactory(recordBuilderFactory);
        validator.setRecordService(recordService);

        final Record.Builder address = recordBuilderFactory.newRecordBuilder();
        address.withString("city", "Sydney");
        address.withString("country", "Australia");

        final Record.Builder builder = recordBuilderFactory.newRecordBuilder();
        builder.withString("last_name", "Smith");
        builder.withString("first_name", "Agent");
        builder.withInt("age", 40);
        builder.withRecord("address", address.build());
        builder.withDateTime("birthdate", ZonedDateTime.parse("1980/07/19 00:00:00+00", JsonToRecord.dtf));

        final Record record = builder.build();

        assertTrue(validator.validate(Config.Condition.EQUALS, expected, record, null));

        /*
         * assertTrue(validator.validate(Config.Condition.CONTAINS, expected, "azerty" + expected + "uiop"));
         * assertFalse(validator.validate(Config.Condition.EQUALS, expected, "azerty" + expected + "uiop"));
         * assertFalse(validator.validate(Config.Condition.CONTAINS, expected, "azertyuiop"));
         */
    }

}