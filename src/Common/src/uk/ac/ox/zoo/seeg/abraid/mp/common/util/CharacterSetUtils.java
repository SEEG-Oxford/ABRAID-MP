package uk.ac.ox.zoo.seeg.abraid.mp.common.util;

import org.mozilla.universalchardet.UniversalDetector;
import org.springframework.util.StringUtils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

/**
 * Contains utilities relating to character sets.
 * Copyright (c) 2014 University of Oxford
 */
public class CharacterSetUtils {
    /**
     * Detects the character set of the input text.
     * @param input The input text as a byte array.
     * @return The character set of the input text, or null if it cannot be detected.
     */
    public static Charset detectCharacterSet(byte[] input) {
        if (input == null) {
            return null;
        }

        Charset charset = null;
        input = input.clone();
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(input, 0, input.length);
        detector.dataEnd();
        String detectedCharset = detector.getDetectedCharset();
        if (StringUtils.hasText(detectedCharset)) {
            try {
                charset = Charset.forName(detectedCharset);
            } catch(UnsupportedCharsetException e) {
                // Intentionally blank
            }
        }
        return charset;
    }

    /**
     * Converts the input text between the two specified character sets.
     * @param input The input text as a byte array.
     * @param fromCharset The source character set.
     * @param toCharSet The destination character set.
     * @return The input text, converted from the source to the destination character set.
     */
    public static byte[] convertToCharacterSet(byte[] input, Charset fromCharset, Charset toCharSet) {
        if (input == null) {
            return null;
        }

        CharBuffer data = fromCharset.decode(ByteBuffer.wrap(input));
        return toCharSet.encode(data).array();
    }
}
