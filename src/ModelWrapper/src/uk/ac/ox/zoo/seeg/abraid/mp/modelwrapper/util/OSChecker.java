package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.util;

/**
 * An interface to provide a means for determining the current system OS.
 * Copyright (c) 2014 University of Oxford
 */
public interface OSChecker {
    /**
     * Determines if the current OS is in the Windows family.
     * @return True if on Windows, otherwise false.
     */
    boolean isWindows();
}
