package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Interface to provide interaction with a VCS repository containing the model code.
 * Copyright (c) 2014 University of Oxford
 */
public interface SourceCodeManager {
    /**
     * Lists the package versions found in the source code repository.
     * @return The versions list.
     * @throws IOException Thrown if the operation could not be completed due to issue accessing local resources.
     * @throws UnsupportedOperationException Thrown if there was an issue interacting with the VCS.
     */
    List<String> getAvailableVersions() throws IOException, UnsupportedOperationException;

    /**
     * Copies a specific version of the source code into the workspace.
     * @param targetDirectory The directory into which the source code should be provisioned.
     * @throws IllegalArgumentException Thrown if the specified version is not found in the repository.
     * @throws IOException Thrown if the operation could not be completed due to issue accessing local resources.
     * @throws UnsupportedOperationException Thrown if there was an issue interacting with the VCS.
     */
    void provision(File targetDirectory)
            throws IllegalArgumentException, IOException, UnsupportedOperationException;

    /**
     * Synchronise the local repository cache with the remote repository url.
     * @throws IOException Thrown if the operation could not be completed due to issue accessing local resources.
     * @throws UnsupportedOperationException Thrown if there was an issue interacting with the VCS.
     */
    void updateRepository() throws IOException, UnsupportedOperationException;

    /**
     * Retrieves the list of model modes supported by the currently selected version of the source code repository.
     * @return The list of modes.
     * @throws IOException Thrown if the operation could not be completed due to issue accessing local resources.
     * @throws UnsupportedOperationException Thrown if there was an issue interacting with the VCS.
     */
    Set<String> getSupportedModesForCurrentVersion() throws IOException, UnsupportedOperationException;
}
