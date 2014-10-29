package uk.ac.ox.zoo.seeg.abraid.mp.common.util;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the CharacterSetUtils class.
 * Copyright (c) 2014 University of Oxford
 */
public class CharacterSetUtilsTest {
    private static final String TEST_FOLDER = "Common/test/uk/ac/ox/zoo/seeg/abraid/mp/common/util";
    private static final String TEST_FILE1_UTF_8 = "file1_utf-8.txt";
    private static final String TEST_FILE1_ISO_8859_1 = "file1_iso-8859-1.txt";
    private static final String TEST_FILE2_UTF_8 = "file2_utf-8.txt";
    private static final String TEST_FILE2_WINDOWS_1252 = "file2_windows-1252.txt";
    private static final Charset WINDOWS_1252_CHARSET = Charset.forName("windows-1252");

    @Test
    public void detectCharacterSetReturnsNullForNullInput() {
        byte[] input = null;
        testDetectCharacterSet(input, null);
    }

    @Test
    public void detectCharacterSetReturnsNullForEmptyInput() {
        byte[] input = new byte[] {};
        testDetectCharacterSet(input, null);
    }

    @Test
    public void detectCharacterSetReturnsNullForASCIIInput() {
        // If there are no special characters then the character set is ambiguous, so null is a reasonable response
        String text = "The quick brown fox jumps over the lazy dog.";
        testDetectCharacterSet(text.getBytes(), null);
    }

    @Test
    public void detectCharacterSetReturnsUTF8ForTestUTF8File1() throws IOException {
        testDetectCharacterSet(TEST_FILE1_UTF_8, StandardCharsets.UTF_8);
    }

    @Test
    public void detectCharacterSetReturnsUTF8ForTestUTF8File2() throws IOException {
        testDetectCharacterSet(TEST_FILE2_UTF_8, StandardCharsets.UTF_8);
    }

    @Test
    public void detectCharacterSetReturnsWindows1252ForTestISO88591File() throws IOException {
        // Windows-1252 is a superset of ISO-8859-1
        testDetectCharacterSet(TEST_FILE1_ISO_8859_1, WINDOWS_1252_CHARSET);
    }

    @Test
    public void detectCharacterSetReturnsWindows1252ForTestWindows1252File() throws IOException {
        testDetectCharacterSet(TEST_FILE2_WINDOWS_1252, WINDOWS_1252_CHARSET);
    }

    @Test
    public void convertToCharacterSetReturnsNullForNullInput() {
        testConvertToCharacterSet((byte[]) null, null, StandardCharsets.ISO_8859_1, StandardCharsets.UTF_8);
    }

    @Test
    public void convertToCharacterSetReturnsEmptyOutputForEmptyInput() {
        byte[] empty = new byte[] {};
        testConvertToCharacterSet(empty, empty, StandardCharsets.ISO_8859_1, StandardCharsets.UTF_8);
    }

    @Test
    public void convertToCharacterSetMakesNoChangeIfSourceAndDestinationCharacterSetsAreTheSame() throws IOException {
        testConvertToCharacterSet(TEST_FILE1_UTF_8, TEST_FILE1_UTF_8, StandardCharsets.UTF_8, StandardCharsets.UTF_8);
    }

    @Test
    public void convertToCharacterSetConvertsISO88591ToUTF8Correctly() throws IOException {
        testConvertToCharacterSet(TEST_FILE1_ISO_8859_1, TEST_FILE1_UTF_8, StandardCharsets.ISO_8859_1, StandardCharsets.UTF_8);
    }

    @Test
    public void convertToCharacterSetConvertsUTF8ToISO88591Correctly() throws IOException {
        testConvertToCharacterSet(TEST_FILE1_UTF_8, TEST_FILE1_ISO_8859_1, StandardCharsets.UTF_8, StandardCharsets.ISO_8859_1);
    }

    @Test
    public void convertToCharacterSetConvertsWindows1252ToUTF8Correctly() throws IOException {
        testConvertToCharacterSet(TEST_FILE2_WINDOWS_1252, TEST_FILE2_UTF_8, WINDOWS_1252_CHARSET, StandardCharsets.UTF_8);
    }

    @Test
    public void convertToCharacterSetConvertsUTF8ToWindows1252Correctly() throws IOException {
        testConvertToCharacterSet(TEST_FILE2_UTF_8, TEST_FILE2_WINDOWS_1252, StandardCharsets.UTF_8, WINDOWS_1252_CHARSET);
    }

    private void testDetectCharacterSet(String inputFilename, Charset expectedCharset) throws IOException {
        byte[] input = FileUtils.readFileToByteArray(new File(TEST_FOLDER, inputFilename));
        testDetectCharacterSet(input, expectedCharset);
    }

    private void testDetectCharacterSet(byte[] input, Charset expectedCharset) {
        Charset charset = CharacterSetUtils.detectCharacterSet(input);
        assertThat(charset).isEqualTo(expectedCharset);
    }

    private void testConvertToCharacterSet(String inputFilename, String expectedOutputFilename, Charset fromCharset,
                                           Charset toCharset) throws IOException {
        byte[] input = FileUtils.readFileToByteArray(new File(TEST_FOLDER, inputFilename));
        byte[] expectedOutput = FileUtils.readFileToByteArray(new File(TEST_FOLDER, expectedOutputFilename));
        testConvertToCharacterSet(input, expectedOutput, fromCharset, toCharset);
    }

    private void testConvertToCharacterSet(byte[] input, byte[] expectedOutput, Charset fromCharset, Charset toCharset) {
        byte[] output = CharacterSetUtils.convertToCharacterSet(input, fromCharset, toCharset);
        assertThat(output).isEqualTo(expectedOutput);
    }
}
