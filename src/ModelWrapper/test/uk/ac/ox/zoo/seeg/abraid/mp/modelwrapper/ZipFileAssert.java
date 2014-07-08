package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper;

import net.lingala.zip4j.core.ZipFile;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.AbstractAssert;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.List;

/**
 * Custom assertions for zip files.
 * Copyright (c) 2014 University of Oxford
 */
public class ZipFileAssert extends AbstractAssert<ZipFileAssert, File> {
    public ZipFileAssert(File actual) {
        super(actual, ZipFileAssert.class);
    }

    public static ZipFileAssert assertThatZip(File actual) {
        return new ZipFileAssert(actual);
    }

    /**
     * Compare the content of a zip file against a list of files.
     *
     * We cannot compare zip files directly (via a byte-by-byte comparison) because there
     * may be trivial differences such as file order, last modified date of files, compression parameters etc.
     * Instead we compare the unzipped contents with existing files.
     *
     * @param expectedZipFileContent The files which should be in the zip.
     * @param testDir A directory which the zip can be extracted for comparison.
     * @return The same ZipFileAssert object, for continued stacked assertions.
     */
    public ZipFileAssert hasContentFiles(List<File> expectedZipFileContent, TemporaryFolder testDir) {
        try {
            // Assert that the zip has the expected number of files
            ZipFile zipFile = new ZipFile(actual.getAbsolutePath());
            int expectedZipFileCount = expectedZipFileContent.size();
            int actualZipFileCount = zipFile.getFileHeaders().size();
            if (actualZipFileCount != expectedZipFileCount) {
                // Number of files in zip file is not as expected
                failWithMessage(String.format("Expected %d files in zip, actual %d files", expectedZipFileCount,
                        actualZipFileCount));
            }

            // Extract all of the files into a temporary folder
            File unzipFolder = testDir.newFolder();
            zipFile.extractAll(unzipFolder.getAbsolutePath());

            // Compare each of the files with those expected
            for (File expectedFile : expectedZipFileContent) {
                String expectedFileName = expectedFile.getName();
                File actualFile = new File(unzipFolder, expectedFileName);
                if (!actualFile.exists()) {
                    // Expected file does not exist in zip file
                    failWithMessage(String.format("Expected file %s does not exist in zip", expectedFileName));
                }

                String expectedFileContents = FileUtils.readFileToString(expectedFile);
                String actualFileContents = FileUtils.readFileToString(actualFile);
                if (!expectedFileContents.equals(actualFileContents)) {
                    // Expected and actual files are not equal
                    failWithMessage(String.format("Unzipped files named %s are not equal.\nExpected: %s\nActual:   %s",
                            expectedFileName, expectedFileContents, actualFileContents));
                }
            }

            return this;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
