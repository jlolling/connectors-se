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
package org.talend.components.assertion.conf;

import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Suggestable;
import org.talend.sdk.component.api.configuration.action.Updatable;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.configuration.type.DataSet;

import org.talend.sdk.component.api.configuration.Option;
import java.io.Serializable;

@Data
@GridLayout({ @GridLayout.Row({ "dse" }) })
@GridLayout(names = GridLayout.FormType.ADVANCED, value = {})
public class Config implements Serializable {

	@Option
	@Documentation("")
	MyDataset dse;

	@GridLayout({@GridLayout.Row({ "test" })})
	@GridLayout(names = GridLayout.FormType.ADVANCED, value = {})
	@Data
	@DataSet("Dataset")
	public static class MyDataset implements Serializable {

		@Option
		@Documentation("")
		MyDatastore test;

	}

	@GridLayout({@GridLayout.Row({ "aaa" })})
	@GridLayout(names = GridLayout.FormType.ADVANCED, value = {})
	@Data
	@DataStore("Datastore")
	public static class MyDatastore implements Serializable {

		@Option
		@Documentation("")
		String aaa;

	}

}
