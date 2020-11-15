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

import org.talend.components.assertion.conf.Config;
import org.talend.components.common.stream.api.input.RecordReader;
import org.talend.components.common.stream.api.input.RecordReaderSupplier;
import org.talend.components.common.stream.format.json.JsonConfiguration;
import org.talend.components.common.stream.format.rawtext.ExtendedRawTextConfiguration;
import org.talend.components.common.stream.input.json.JsonToRecord;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.record.RecordVisitor;

import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public class RecordValidator extends Validator<Record> {

    @Override
    public boolean validate(Config.Condition condition, String expected, Record value, Record record) {
        final JsonToRecord jsonToRecord = new JsonToRecord(this.getRecordBuilderFactory(), false, true);

        InputStream expectedInputStream = new ByteArrayInputStream(expected.getBytes());
        final JsonReader reader = this.getJsonReaderFactory().createReader(expectedInputStream);
        final JsonObject jsexpected = reader.readObject();
        final Record rexpected = jsonToRecord.toRecord(jsexpected);

        if (condition == Config.Condition.EQUALS) {
            return recordEquals(rexpected, value);
        }

        /*
         * switch (condition) {
         * case EQUALS:
         * return expected.equals(value);
         * case CONTAINS:
         * return value.contains(expected);
         * default:
         * throw new UnsupportedOperationException("Unspported String condition : " + condition);
         * }
         */

        return true;
    }

    public final boolean recordEquals(Record r1, Record r2) {
        final Boolean equals = this.getRecordService().visit(new CompareRecordVisitor(r1), r2);
        return equals;
    }

    private class CompareRecordVisitor implements RecordVisitor<Boolean> {

        private Record toCompareWith;

        private Boolean equals = true;

        public CompareRecordVisitor(final Record toCompareWith) {
            this.toCompareWith = toCompareWith;
        }

        private void setEquals(boolean b) {
            this.equals = equals && b; // if it's false, it stays false
        }

        @Override
        public void onInt(final Schema.Entry entry, final OptionalInt optionalInt) {
            final OptionalInt toCompareInt = this.toCompareWith.getOptionalInt(entry.getName());
            if (optionalInt.isPresent() && toCompareInt.isPresent()) {
                this.setEquals(optionalInt.getAsInt() == toCompareInt.getAsInt());
            } else if (optionalInt.isPresent() != toCompareInt.isPresent()) {
                this.setEquals(false);
            }
        }

        public void onDouble(final Schema.Entry entry, final OptionalDouble optionalDouble) {
            final OptionalDouble toCompareDouble = this.toCompareWith.getOptionalDouble(entry.getName());
            if (optionalDouble.isPresent() && toCompareDouble.isPresent()) {
                this.setEquals(optionalDouble.getAsDouble() == toCompareDouble.getAsDouble());
            } else if (optionalDouble.isPresent() != toCompareDouble.isPresent()) {
                this.setEquals(false);
            }
        }

        @Override
        public Boolean get() {
            return this.equals;
        }

        @Override
        public Boolean apply(final Boolean b1, final Boolean b2) {
            return b1 && b2;
        }

        public void onString(final Schema.Entry entry, final Optional<String> optionalString) {
            final Optional<String> toCompareString = this.toCompareWith.getOptionalString(entry.getName());

            if (optionalString.isPresent() && toCompareString.isPresent()) {
                this.setEquals(optionalString.get().equals(toCompareString.get()));
            } else if (optionalString.isPresent() != toCompareString.isPresent()) {
                this.setEquals(false);
            }
        }

        public void onDatetime(final Schema.Entry entry, final Optional<ZonedDateTime> optionalDateTime) {
            final Optional<ZonedDateTime> toCompareDateTime = this.toCompareWith.getOptionalDateTime(entry.getName());

            if (optionalDateTime.isPresent() && toCompareDateTime.isPresent()) {
                this.setEquals(optionalDateTime.get().compareTo(toCompareDateTime.get()) == 0);
            } else if (optionalDateTime.isPresent() != toCompareDateTime.isPresent()) {
                this.setEquals(false);
            }
        }

        /*
         * default void onLong(final Schema.Entry entry, final OptionalLong optionalLong) {
         * // no-op
         * }
         * 
         * default void onFloat(final Schema.Entry entry, final OptionalDouble optionalFloat) {
         * // no-op
         * }
         * 
         * default void onDouble(final Schema.Entry entry, final OptionalDouble optionalDouble) {
         * // no-op
         * }
         * 
         * default void onBoolean(final Schema.Entry entry, final Optional<Boolean> optionalBoolean) {
         * // no-op
         * }
         * 
         * default void onString(final Schema.Entry entry, final Optional<String> string) {
         * // no-op
         * }
         * 
         * default void onDatetime(final Schema.Entry entry, final Optional<ZonedDateTime> dateTime) {
         * // no-op
         * }
         * 
         * default void onBytes(final Schema.Entry entry, final Optional<byte[]> bytes) {
         * // no-op
         * }
         */

        public RecordVisitor<Boolean> onRecord(final Schema.Entry entry, final Optional<Record> record) {
            Optional<Record> optRec = toCompareWith.getOptionalRecord(entry.getName());

            if (record.isPresent() != optRec.isPresent()) {
                this.setEquals(false);
            }

            return new CompareRecordVisitor(optRec.get());
        }

        /*
         * default void onIntArray(final Schema.Entry entry, final Optional<Collection<Integer>> array) {
         * // no-op
         * }
         * 
         * default void onLongArray(final Schema.Entry entry, final Optional<Collection<Long>> array) {
         * // no-op
         * }
         * 
         * default void onFloatArray(final Schema.Entry entry, final Optional<Collection<Float>> array) {
         * // no-op
         * }
         * 
         * default void onDoubleArray(final Schema.Entry entry, final Optional<Collection<Double>> array) {
         * // no-op
         * }
         * 
         * default void onBooleanArray(final Schema.Entry entry, final Optional<Collection<Boolean>> array) {
         * // no-op
         * }
         * 
         * default void onStringArray(final Schema.Entry entry, final Optional<Collection<String>> array) {
         * // no-op
         * }
         * 
         * default void onDatetimeArray(final Schema.Entry entry, final Optional<Collection<ZonedDateTime>> array) {
         * // no-op
         * }
         * 
         * default void onBytesArray(final Schema.Entry entry, final Optional<Collection<byte[]>> array) {
         * // no-op
         * }
         * 
         * default RecordVisitor<Boolean> onRecordArray(final Schema.Entry entry, final Optional<Collection<Record>> array) {
         * return this;
         * }
         */
    }
}
