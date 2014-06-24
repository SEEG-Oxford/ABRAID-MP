package uk.ac.ox.zoo.seeg.abraid.mp.common.util;

import org.springframework.util.StringUtils;

/**
 * Utilities for parsing.
 *
 * Copyright (c) 2014 University of Oxford
 */
public final class ParseUtils {
    private ParseUtils() {
    }

    /**
     * Parses a string into an integer. Trims whitespace and does not throw a NumberFormatException.
     * @param toParse The string to parse.
     * @return The parsed integer, or null if it could not be parsed.
     */
    public static Integer parseInteger(String toParse) {
        try {
            return Integer.parseInt(convertString(toParse));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Parses a string into a long. Trims whitespace and does not throw a NumberFormatException.
     * @param toParse The string to parse.
     * @return The parsed long, or null if it could not be parsed.
     */
    public static Long parseLong(String toParse) {
        try {
            return Long.parseLong(convertString(toParse));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Parses a string into a double. Trims whitespace and does not throw a NumberFormatException.
     * @param toParse The string to parse.
     * @return The parsed double, or null if it could not be parsed.
     */
    public static Double parseDouble(String toParse) {
        String convertedString = convertString(toParse);

        // Null input causes Double.parseDouble to throw a NullPointerException, so handle this separately
        if (convertedString == null) {
            return null;
        }

        try {
            return Double.parseDouble(convertedString);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Converts a string to a canonical form, by stripping whitespace and converting to a null if an empty string.
     * @param toConvert The string to convert.
     * @return The converted string.
     */
    public static String convertString(String toConvert) {
        String convertedString = StringUtils.trimWhitespace(toConvert);
        if (convertedString != null && convertedString.equals("")) {
            convertedString = null;
        }
        return convertedString;
    }
}
