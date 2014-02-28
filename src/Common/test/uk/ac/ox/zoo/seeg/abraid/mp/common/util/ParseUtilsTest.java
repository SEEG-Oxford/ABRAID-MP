package uk.ac.ox.zoo.seeg.abraid.mp.common.util;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the ParseUtils class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ParseUtilsTest {
    @Test
    public void parseValidInteger() {
        int parsedInteger = ParseUtils.parseInteger("1234");
        assertThat(parsedInteger).isEqualTo(1234);
    }

    @Test
    public void parseWhitespaceIntoInteger() {
        Integer parsedInteger = ParseUtils.parseInteger("    \t");
        assertThat(parsedInteger).isNull();
    }
    
    @Test
    public void parseInvalidInteger() {
        Integer parsedInteger = ParseUtils.parseInteger("undefined");
        assertThat(parsedInteger).isNull();
    }

    @Test
    public void parseValidDouble() {
        double parsedDouble = ParseUtils.parseDouble("1234.56789");
        assertThat(parsedDouble).isEqualTo(1234.56789);
    }

    @Test
    public void parseWhitespaceIntoDouble() {
        Double parsedDouble = ParseUtils.parseDouble("    \t");
        assertThat(parsedDouble).isNull();
    }

    @Test
    public void parseInvalidDouble() {
        Double parsedDouble = ParseUtils.parseDouble("undefined");
        assertThat(parsedDouble).isNull();
    }

    @Test
    public void parseValidLong() {
        long parsedLong = ParseUtils.parseLong("100000000000");
        assertThat(parsedLong).isEqualTo(100000000000L);
    }

    @Test
    public void parseWhitespaceIntoLong() {
        Long parsedLong = ParseUtils.parseLong("    \t");
        assertThat(parsedLong).isNull();
    }

    @Test
    public void parseInvalidLong() {
        Long parsedLong = ParseUtils.parseLong("undefined");
        assertThat(parsedLong).isNull();
    }
}
