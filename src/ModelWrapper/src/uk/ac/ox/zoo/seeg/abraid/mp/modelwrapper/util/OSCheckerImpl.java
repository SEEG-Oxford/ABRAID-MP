package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.util;

import org.apache.commons.exec.OS;

/**
 * Uses commons-exec to implement the OSChecker interface.
 * Copyright (c) 2014 University of Oxford
 */
public class OSCheckerImpl implements OSChecker {
    /**
     * Determines if the current OS is in the Windows family.
     * @return True if on Windows, otherwise false.
     */
    @Override
    public boolean isWindows() {
        return OS.isFamilyWindows();
    }
}
