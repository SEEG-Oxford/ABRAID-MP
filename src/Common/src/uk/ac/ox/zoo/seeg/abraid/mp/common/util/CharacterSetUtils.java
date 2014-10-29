package uk.ac.ox.zoo.seeg.abraid.mp.common.util;

import org.mozilla.universalchardet.UniversalDetector;
import org.springframework.util.StringUtils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;

/**
 * Contains utilities relating to character sets.
 * Copyright (c) 2014 University of Oxford
 */
public final class CharacterSetUtils {
    private CharacterSetUtils() {
    }

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
            } catch (UnsupportedCharsetException e) {
                throw new RuntimeException("Detected unsupported character set " + detectedCharset);
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

        CharBuffer decodedData = fromCharset.decode(ByteBuffer.wrap(input));
        ByteBuffer encodedData = toCharSet.encode(decodedData);
        return Arrays.copyOf(encodedData.array(), encodedData.limit());
    }
}
