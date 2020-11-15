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
package org.talend.components.common.stream.input.json;

import java.io.StringReader;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.talend.components.common.stream.output.json.RecordToJson;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.record.Schema.Entry;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.runtime.record.RecordBuilderFactoryImpl;

class JsonToRecordTest {

    final JsonObject jsonObject = Json.createObjectBuilder().add("Hello", "World")
            .add("array", Json.createArrayBuilder().add("First"))
            .add("arrayOfObject",
                    Json.createArrayBuilder().add(Json.createObjectBuilder().add("f1", "v1"))
                            .add(Json.createObjectBuilder().add("f1", "v2").add("f2", "v2f2").addNull("f3")))
            .add("arrayOfArray",
                    Json.createArrayBuilder().add(Json.createArrayBuilder().add(20.0).add(30.0).add(40.0))
                            .add(Json.createArrayBuilder().add(11.0).add(12.0).add(13.0)))
            .add("subRecord", Json.createObjectBuilder().add("field_1", "val1").add("field_2", "val2")).build();

    private JsonToRecord toRecord;

    private JsonToRecord toRecordDoubleOption;

    private JsonToRecord toRecordForceType;

    @BeforeAll
    static void initLog() {
        System.setProperty("org.slf4j.simpleLogger.log.org.talend.components.common.stream", "debug");
    }

    @BeforeEach
    void start() {
        final RecordBuilderFactory recordBuilderFactory = new RecordBuilderFactoryImpl("test");
        this.toRecord = new JsonToRecord(recordBuilderFactory);
        this.toRecordDoubleOption = new JsonToRecord(recordBuilderFactory, true);
        this.toRecordForceType = new JsonToRecord(recordBuilderFactory, false, true);
    }

    @Test
    void toRecord() {
        final Record record = toRecord.toRecord(this.jsonObject);
        Assertions.assertNotNull(record);

        final RecordToJson toJson = new RecordToJson();
        final JsonObject jsonResult = toJson.fromRecord(record);
        Assertions.assertNotNull(jsonResult);

        // object equals except for 'null' value
        Assertions.assertEquals(this.jsonObject.getString("Hello"), jsonResult.getString("Hello"));
        Assertions.assertEquals(this.jsonObject.getJsonArray("array"), jsonResult.getJsonArray("array"));
        Assertions.assertEquals(this.jsonObject.getJsonArray("arrayOfArray"), jsonResult.getJsonArray("arrayOfArray"));
        Assertions.assertEquals(this.jsonObject.getJsonObject("subRecord"), jsonResult.getJsonObject("subRecord"));

        final JsonArray array = this.jsonObject.getJsonArray("arrayOfObject");
        final JsonArray resultArray = jsonResult.getJsonArray("arrayOfObject");

        Assertions.assertEquals(array.get(0).asJsonObject().getString("f1"), resultArray.get(0).asJsonObject().getString("f1"));
        Assertions.assertEquals(array.get(1).asJsonObject().getString("f1"), resultArray.get(1).asJsonObject().getString("f1"));
        Assertions.assertEquals(array.get(1).asJsonObject().getString("f2"), resultArray.get(1).asJsonObject().getString("f2"));
    }

    @Test
    void toRecordWithDollarChar() {
        JsonObject jsonWithDollarChar = getJsonObject(
                "{\"_id\": {\"$oid\": \"5e66158f6eddd6049f309ddb\"}, \"date\": {\"$date\": 1543622400000}, \"item\": \"Cake - Chocolate\", \"quantity\": 2.0, \"amount\": {\"$numberDecimal\": \"60\"}}");
        final Record record = toRecord.toRecord(jsonWithDollarChar);
        Assertions.assertNotNull(record);
        Assertions.assertNotNull(record.getRecord("_id").getString("oid"));
    }

    @Test
    void toRecordWithHyphen() {
        JsonObject jsonWithDollarChar = getJsonObject("{\"_id\": {\"Content-Type\" : \"text/plain\"}}");
        final Record record = toRecord.toRecord(jsonWithDollarChar);
        Assertions.assertNotNull(record);
        Assertions.assertNotNull("text/plain", record.getRecord("_id").getString("Content_Type"));
    }

    @Test
    void numberToRecord() {
        String source = "{\n \"aNumber\" : 7,\n \"aaa\" : [1, 2, 3.0]\n}";
        JsonObject json = getJsonObject(source);
        final Record record = toRecord.toRecord(json);

        Assertions.assertNotNull(record);
        final Entry aaaEntry = findEntry(record.getSchema(), "aaa");
        Assertions.assertNotNull(aaaEntry);

        Assertions.assertEquals(Schema.Type.ARRAY, aaaEntry.getType());
        Assertions.assertEquals(Schema.Type.LONG, aaaEntry.getElementSchema().getType());

        final Entry aNumberEntry = findEntry(record.getSchema(), "aNumber");
        Assertions.assertEquals(Schema.Type.LONG, aNumberEntry.getType());

        RecordToJson toJson = new RecordToJson();
        final JsonObject jsonObject = toJson.fromRecord(record);
        Assertions.assertNotNull(jsonObject);

        final Record recordDouble = toRecordDoubleOption.toRecord(json);
        Assertions.assertNotNull(recordDouble);
        final Entry aaaEntryDouble = findEntry(recordDouble.getSchema(), "aaa");
        Assertions.assertNotNull(aaaEntry);

        Assertions.assertEquals(Schema.Type.ARRAY, aaaEntryDouble.getType());
        Assertions.assertEquals(Schema.Type.DOUBLE, aaaEntryDouble.getElementSchema().getType());

        final Entry aNumberEntryDouble = findEntry(recordDouble.getSchema(), "aNumber");
        Assertions.assertEquals(Schema.Type.DOUBLE, aNumberEntryDouble.getType());
    }

    @Test
    void testForceType() {
        String source = "{\"is_int\" : \"__INT__10\", \"is_long\" : \"__LONG__10\", \"is_float\" : \"__FLOAT__10\", \"is_double\" : \"__DOUBLE__10\", \"is_bytes\" : \"__BYTES__000102030A7F80FFFEFDF6\", \"is_datetime\" : \"__DATETIME__2020/10/01 00:06:00+02\"}";
        JsonObject json = getJsonObject(source);
        final Record record = toRecordForceType.toRecord(json);

        final Entry is_int = findEntry(record.getSchema(), "is_int");
        Assertions.assertEquals(Schema.Type.INT, is_int.getType());
        Assertions.assertEquals(10, record.getInt("is_int"));

        final Entry is_long = findEntry(record.getSchema(), "is_long");
        Assertions.assertEquals(Schema.Type.LONG, is_long.getType());
        Assertions.assertEquals(10L, record.getLong("is_long"));

        final Entry is_float = findEntry(record.getSchema(), "is_float");
        Assertions.assertEquals(Schema.Type.FLOAT, is_float.getType());
        Assertions.assertEquals(10f, record.getFloat("is_float"));

        final Entry is_double = findEntry(record.getSchema(), "is_double");
        Assertions.assertEquals(Schema.Type.DOUBLE, is_double.getType());
        Assertions.assertEquals(10d, record.getDouble("is_double"));

        final Entry is_bytes = findEntry(record.getSchema(), "is_bytes");
        Assertions.assertEquals(Schema.Type.BYTES, is_bytes.getType());
        Assertions.assertArrayEquals(new byte[] { 0, 1, 2, 3, 10, 127, -128, -1, -2, -3, -10 }, record.getBytes("is_bytes"));

        final Entry is_datetime = findEntry(record.getSchema(), "is_datetime");
        Assertions.assertEquals(Schema.Type.DATETIME, is_datetime.getType());

        final String DEFAULT_DATE_FORMAT = "yyyy/MM/dd HH:mm:ssX";
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT).withZone(ZoneId.of("UTC"));
        Assertions.assertEquals(ZonedDateTime.parse("2020/10/01 00:06:00+02", dtf), record.getDateTime("is_datetime"));
    }

    private Entry findEntry(Schema schema, String entryName) {
        return schema.getEntries().stream().filter((Entry e) -> entryName.equals(e.getName())).findFirst().orElse(null);
    }

    private JsonObject getJsonObject(String content) {
        try (JsonReader reader = Json.createReader(new StringReader(content))) {
            return reader.readObject();
        }
    }
}