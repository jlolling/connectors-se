package org.talend.components.assertion.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.talend.components.assertion.conf.Config;

import static org.junit.jupiter.api.Assertions.*;

class DateValidatorTest {

    private DateValidator validator;
    private String expected;

    @BeforeEach
    public void beforeEach(){
        validator = new DateValidator();
        expected = "2020/10/01 00:06:00+02";
    }

    @Test
    public void valideStringEquals(){
        String value = "2020/10/01 00:06:00+02";
        assertTrue(validator.validate(Config.Condition.EQUALS, expected, value));
        assertFalse(validator.validate(Config.Condition.INFERIOR, expected, value));
        assertFalse(validator.validate(Config.Condition.SUPERIOR, expected, value));
    }

    @Test
    public void valideStringSuperior(){
        String value = "2020/10/01 00:06:01+02";
        assertFalse(validator.validate(Config.Condition.EQUALS, expected, value));
        assertFalse(validator.validate(Config.Condition.INFERIOR, expected, value));
        assertTrue(validator.validate(Config.Condition.SUPERIOR, expected, value));
    }

    @Test
    public void valideStringInferior(){
        String value = "2020/10/01 00:05:59+02";
        assertFalse(validator.validate(Config.Condition.EQUALS, expected, value));
        assertTrue(validator.validate(Config.Condition.INFERIOR, expected, value));
        assertFalse(validator.validate(Config.Condition.SUPERIOR, expected, value));
    }

    @Test
    public void valideWithFormat(){
        String format = "dd-MM-yyyy | ss:HH:mmO";
        expected = "01-10-2020 | 05:00:59GMT+8";
        String value = "30-09-2020 | 05:00:59GMT+8";

        validator.setFormat(format);
        assertFalse(validator.validate(Config.Condition.EQUALS, expected, value));
        assertTrue(validator.validate(Config.Condition.INFERIOR, expected, value));
        assertFalse(validator.validate(Config.Condition.SUPERIOR, expected, value));
    }
}