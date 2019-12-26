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
package org.talend.components.magentocms.input;

import lombok.Data;
import org.talend.components.magentocms.common.MagentoDataSet;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;

@Data
@GridLayout({ @GridLayout.Row({ "magentoDataSet" }), @GridLayout.Row({ "selectionFilter" }) })
@GridLayout(names = GridLayout.FormType.ADVANCED, value = { @GridLayout.Row({ "magentoDataSet" }),
        @GridLayout.Row({ "selectionFilter" }) })
@Documentation("Input component configuration")
public class MagentoInputConfiguration implements Serializable {

    @Option
    @Documentation("Connection to Magento CMS")
    private MagentoDataSet magentoDataSet = new MagentoDataSet();

    @Option
    @Documentation("Data filter")
    private ConfigurationFilter selectionFilter = new ConfigurationFilter();

}