package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import java.util.List;

/**
 * Service interface for model run inputs and outputs.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface ModelRunService {
    /**
     * Gets disease occurrences for a request to run the model.
     * @param diseaseGroupId The ID of the disease group.
     * @return Disease occurrences for a request to run the model.
     */
    List<DiseaseOccurrence> getDiseaseOccurrencesForModelRunRequest(Integer diseaseGroupId);

    /**
     * Gets a model run by name.
     * @param name The model run name.
     * @return The model run with the specified name, or null if no model run.
     */
    ModelRun getModelRunByName(String name);

    /**
     * Saves a model run.
     * @param modelRun The model run to save.
     */
    void saveModelRun(ModelRun modelRun);

    /**
     * Updates the specified model run to include the specified mean prediction raster.
     * @param modelRunId The model run's ID.
     * @param gdalRaster The mean prediction raster, in any GDAL format supported by the PostGIS database.
     */
    void updateMeanPredictionRasterForModelRun(int modelRunId, byte[] gdalRaster);

    /**
     * Updates the specified model run to include the specified prediction uncertainty raster.
     * @param modelRunId The model run's ID.
     * @param gdalRaster The prediction uncertainty raster, in any GDAL format supported by the PostGIS database.
     */
    void updatePredictionUncertaintyRasterForModelRun(int modelRunId, byte[] gdalRaster);
}
