package uk.ac.ox.zoo.seeg.abraid.mp.common.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * File utilities.
 *
 * Copyright (c) 2014 University of Oxford
 */
public final class FileUtils {
    private FileUtils() {
    }

    /**
     * Loads the specified file into a string.
     * @param fileName The full path to the file.
     * @param charset The file's character set.
     * @return The file as a string.
     * @throws IOException if an I/O error occurs reading from the stream
     */
    public static String loadFileIntoString(String fileName, Charset charset) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(fileName));
        return charset.decode(ByteBuffer.wrap(encoded)).toString();
    }
}
