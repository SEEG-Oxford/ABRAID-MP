package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.tools;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractAsynchronousActionHandler;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Asynchronous wrapper for UploadCsvControllerHelper.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class UploadCsvControllerHelperAsyncWrapper extends AbstractAsynchronousActionHandler {
    private static final Logger LOGGER = Logger.getLogger(UploadCsvController.class);
    private static final int THREAD_POOL_SIZE = 3;

    private UploadCsvControllerHelper uploadCsvControllerHelper;

    @Autowired
    public UploadCsvControllerHelperAsyncWrapper(UploadCsvControllerHelper uploadCsvControllerHelper) {
        super(THREAD_POOL_SIZE);
        this.uploadCsvControllerHelper = uploadCsvControllerHelper;
    }

    /**
     * Acquires the supplied CSV data. Sends an e-mail when completed (either successfully or unsuccessfully).
     *
     * @param csv The contents of the CSV file to upload.
     * @param userEmailAddress The e-mail address of the user that submitted the upload.
     * @param filePath The full path to the file to upload (used for information only).
     * @return A future for the background operation.
     */
    public Future acquireCsvData(final String csv, final String userEmailAddress,
                                 final String filePath) {
        return submitAsynchronousTask(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try {
                    uploadCsvControllerHelper.acquireCsvData(csv, userEmailAddress, filePath);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
                return null;
            }
        });
    }
}
