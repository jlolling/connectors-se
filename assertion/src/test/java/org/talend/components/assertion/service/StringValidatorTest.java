package org.talend.components.assertion.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.talend.components.assertion.conf.Config;

import static org.junit.jupiter.api.Assertions.*;

class StringValidatorTest {

    private StringValidator validator;

    @BeforeEach
    public void beforeEach(){
        validator = new StringValidator();
    }

    @Test
    public void validate(){
        String expected = "expected";
        assertTrue(validator.validate(Config.Condition.EQUALS, expected, expected));
        assertTrue(validator.validate(Config.Condition.CONTAINS, expected, "azerty"+expected+"uiop"));
        assertFalse(validator.validate(Config.Condition.EQUALS, expected, "azerty"+expected+"uiop"));
        assertFalse(validator.validate(Config.Condition.CONTAINS, expected, "azertyuiop"));
    }

}