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
package org.talend.components.bigquery.service;

import org.talend.sdk.component.api.internationalization.Internationalized;

@Internationalized
public interface I18nMessage {

    String successConnection();

    String cannotCreateBigQueryClient();

    String schemaNotDefined();

    String entryTypeNotDefined(String message);

    String bigQueryTypeNotDefined(String message);

    String tableOperationIsNotSupported(String message);

    String writeOperationIsNotSupported(String message);

    String errorQueryExecution();

    String errorBlobReaderInit();

    String errorSplit();

    String nbMappers();

    String blobsPrefix();

    String infoTableNoExists();

    String errorCreationTable();

    String infoTableCreated();

    String warnRejected();
}
