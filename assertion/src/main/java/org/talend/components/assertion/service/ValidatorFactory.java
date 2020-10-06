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

public class ValidatorFactory {

    private ValidatorFactory() {
    }

    public static Validator createInstance(final Config.AssertEntry asrt, String dateFormat) {

        if (asrt.getCondition() == Config.Condition.IS_NULL) {
            return new NullValidator();
        }

        final Config.Type type = asrt.getType();

        switch (type) {
        case STRING:
            return new StringValidator();
        case BOOLEAN:
            return new BooleanValidator();
        case NUMBER:
            return new NumberValidator();
        case DATE:
            final DateValidator dateValidator = new DateValidator();
            dateValidator.setFormat(dateFormat);
            return dateValidator;
        default:
            throw new UnsupportedOperationException("Can't validate type " + type);
        }

    }

}
