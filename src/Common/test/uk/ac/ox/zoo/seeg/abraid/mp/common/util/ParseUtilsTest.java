package uk.ac.ox.zoo.seeg.abraid.mp.common.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
    public void parseNullInteger() {
        Integer parsedInteger = ParseUtils.parseInteger(null);
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
    public void parseNullDouble() {
        Double parsedDouble = ParseUtils.parseDouble(null);
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

    @Test
    public void parseNullLong() {
        Long parsedLong = ParseUtils.parseLong(null);
        assertThat(parsedLong).isNull();
    }

    @Test
    public void convertStringWithWhitespace() {
        String convertedString = ParseUtils.convertString("   test string  ");
        assertThat(convertedString).isEqualTo("test string");
    }

    @Test
    public void convertEmptyString() {
        String convertedString = ParseUtils.convertString("");
        assertThat(convertedString).isNull();
    }

    @Test
    public void convertNullString() {
        String convertedString = ParseUtils.convertString(null);
        assertThat(convertedString).isNull();
    }

    @Test
    public void parseNullListOfIntegers() {
        List<Integer> parsedIntegers = ParseUtils.parseIntegers(null);
        assertThat(parsedIntegers).isNull();
    }

    @Test
    public void parseValidAndInvalidIntegers() {
        List<Integer> parsedIntegers = ParseUtils.parseIntegers(Arrays.asList("1", null, "2", "text", "3"));
        assertThat(parsedIntegers).containsExactly(1, 2, 3);
    }

    @Test
    public void convertNullListOfStrings() {
        List<String> convertedStrings = ParseUtils.convertStrings(null);
        assertThat(convertedStrings).isNull();
    }

    @Test
    public void convertStrings() {
        List<String> convertedStrings = ParseUtils.convertStrings(Arrays.asList("   first string ", null, "", "second"));
        assertThat(convertedStrings).containsExactly("first string", null, null, "second");
    }

    @Test
    public void splitCommaDelimitedStringWhenNull() {
        assertThat(ParseUtils.splitCommaDelimitedString(null)).isEmpty();
    }

    @Test
    public void splitCommaDelimitedStringWhenNonNull() {
        String text = "  One , two,,\nthree,    , four,";
        List<String> split = ParseUtils.splitCommaDelimitedString(text);
        assertThat(split).containsExactly("One", "two", "three", "four");
    }
}
