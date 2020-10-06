package org.talend.components.assertion.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.talend.components.assertion.conf.Config;

import static org.junit.jupiter.api.Assertions.*;

class NumberValidatorTest {

    private NumberValidator validator;

    @BeforeEach
    public void beforeEach(){
        validator = new NumberValidator();
    }

    @Test
    public void validateEquals(){
        String expected = "12345.2589";

        assertTrue(validator.validate(Config.Condition.EQUALS, expected,expected));
        assertTrue(validator.validate(Config.Condition.EQUALS, expected, 12345.2589d));
        //assertTrue(validator.validate(Config.Condition.EQUALS, expected, 12345.2589f)); // float precision error
        assertFalse(validator.validate(Config.Condition.EQUALS, expected, "12345.2588"));
        assertFalse(validator.validate(Config.Condition.EQUALS, expected, 12345.2589d - 0.0001d));
    }

    @Test
    public void validateInferior() {
        String expected = "12345.2589";

        assertTrue(validator.validate(Config.Condition.INFERIOR, expected, "12345.2588"));
        assertTrue(validator.validate(Config.Condition.INFERIOR, expected, 12345.2589d - 0.0001d));

        assertFalse(validator.validate(Config.Condition.INFERIOR, expected, "12345.2590"));
        assertFalse(validator.validate(Config.Condition.INFERIOR, expected, 12345.2589d + 0.0001d));
    }

    @Test
    public void validateSuperior() {
        String expected = "12345.2589";

        assertTrue(validator.validate(Config.Condition.SUPERIOR, expected, "12345.2590"));
        assertTrue(validator.validate(Config.Condition.SUPERIOR, expected, 12345.2589d + 0.0001d));

        assertFalse(validator.validate(Config.Condition.SUPERIOR, expected, "12345.2588"));
        assertFalse(validator.validate(Config.Condition.SUPERIOR, expected, 12345.2589d - 0.0001d));

    }

}