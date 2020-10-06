package org.talend.components.assertion.service;

import org.junit.jupiter.api.Test;
import org.talend.components.assertion.conf.Config;

import static org.junit.jupiter.api.Assertions.*;

class NullValidatorTest {

    @Test
    public void validate(){
        NullValidator validator = new NullValidator();
        assertTrue(validator.validate(Config.Condition.EQUALS, "", null));
        assertFalse(validator.validate(Config.Condition.EQUALS, "", "not null"));
    }

}