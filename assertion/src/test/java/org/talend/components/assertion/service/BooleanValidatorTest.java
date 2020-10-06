package org.talend.components.assertion.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.talend.components.assertion.conf.Config;

import static org.junit.jupiter.api.Assertions.*;

class BooleanValidatorTest {

    private BooleanValidator validator;

    @BeforeEach
    public void beforeEach(){
        validator = new BooleanValidator();
    }

    @Test
    public void validate(){
        assertTrue(validator.validate(Config.Condition.EQUALS, "True", Boolean.TRUE));
        assertFalse(validator.validate(Config.Condition.EQUALS, "True", Boolean.FALSE));
        assertTrue(validator.validate(Config.Condition.EQUALS, "False", Boolean.FALSE));
        assertFalse(validator.validate(Config.Condition.EQUALS, "False", Boolean.TRUE));
    }

}