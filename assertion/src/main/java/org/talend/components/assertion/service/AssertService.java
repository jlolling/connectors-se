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

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.talend.components.assertion.conf.Config;
import org.talend.components.common.stream.api.RecordIORepository;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.RecordPointer;
import org.talend.sdk.component.api.record.RecordPointerFactory;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.api.service.record.RecordService;

import javax.json.JsonReaderFactory;
import javax.json.stream.JsonParserFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Data
public class AssertService {

    public final static String LOG_PREFIX = "ASSERT - ";

    @Service
    private JsonReaderFactory jsonReaderFactory;

    @Service
    private JsonParserFactory jsonParserFactory;

    @Service
    private RecordPointerFactory recordPointerFactory;

    @Service
    private RecordBuilderFactory recordBuilderFactory;

    @Service
    private RecordService recordService;

    public List<String> validate(final Config config, final Record record) {
        return validateAssertionsOnRecord(config.getAssertions(), record, config.getDateFormat());
    }

    private List<String> validateAssertionsOnRecord(final List<Config.AssertEntry> assertions, final Record record,
            String dateFormat) {
        List<String> errors = new ArrayList<>();

        for (Config.AssertEntry asrt : assertions) {
            final Optional<String> s = validateOneAssertionOnrecord(asrt, record, dateFormat);
            s.ifPresent(e -> errors.add(e));
        }

        return errors;
    }

    private Optional<String> validateOneAssertionOnrecord(final Config.AssertEntry asrt, final Record record, String dateFormat) {

        final Validator validator = ValidatorFactory.createInstance(asrt, dateFormat);
        validator.setRecordBuilderFactory(recordBuilderFactory);
        validator.setRecordService(recordService);
        validator.setRecordPointerFactory(recordPointerFactory);
        validator.setJsonReaderFactory(jsonReaderFactory);
        validator.setJsonParserFactory(jsonParserFactory);

        final RecordPointer pointer = recordPointerFactory.apply(asrt.getPath());
        try {
            final Object value = pointer.getValue(record, Util.getClassFromType(asrt.getType()));
            final boolean validate = validator.validate(asrt.getCondition(), asrt.getValue(), value, record);
            String err = this.buildErrorMsg(validate, asrt, value);
            return Optional.ofNullable(err);
        } catch (IllegalArgumentException | ClassCastException e) {
            return Optional.ofNullable("Can't find '" + asrt.getType() + "' value for path '" + asrt.getPath() + "'");
        }
    }

    private String buildErrorMsg(boolean validate, Config.AssertEntry asrt, Object value) {
        if (validate) {
            log.info(AssertService.LOG_PREFIX + asrt + " with retrieved value '" + value + "' : SUCCESSFUL");
            return null;
        }

        return asrt + "\nRetrieved value was : " + value;

    }

}
