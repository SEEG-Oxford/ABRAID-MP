package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.DiseaseOccurrenceDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.ModelRunDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.NativeSQL;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.NativeSQLImpl;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import java.util.List;

/**
 * Service interface for model run inputs and outputs.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Transactional(rollbackFor = Exception.class)
public class ModelRunServiceImpl implements ModelRunService {
    private DiseaseOccurrenceDao diseaseOccurrenceDao;
    private ModelRunDao modelRunDao;
    private NativeSQL nativeSQL;

    public ModelRunServiceImpl(DiseaseOccurrenceDao diseaseOccurrenceDao, ModelRunDao modelRunDao,
                               NativeSQL nativeSQL) {
        this.diseaseOccurrenceDao = diseaseOccurrenceDao;
        this.modelRunDao = modelRunDao;
        this.nativeSQL = nativeSQL;
    }

    /**
     * Gets disease occurrences for a request to run the model.
     * @param diseaseGroupId The ID of the disease group.
     * @return Disease occurrences for a request to run the model.
     */
    @Override
    public List<DiseaseOccurrence> getDiseaseOccurrencesForModelRunRequest(Integer diseaseGroupId) {
        return diseaseOccurrenceDao.getDiseaseOccurrencesForModelRunRequest(diseaseGroupId);
    }

    /**
     * Gets a model run by name.
     * @param name The model run name.
     * @return The model run with the specified name, or null if no model run.
     */
    public ModelRun getModelRunByName(String name) {
        return modelRunDao.getByName(name);
    }

    /**
     * Saves a model run.
     * @param modelRun The model run to save.
     */
    public void saveModelRun(ModelRun modelRun) {
        modelRunDao.save(modelRun);
    }

    /**
     * Updates the specified model run to include the specified mean prediction raster.
     * @param modelRunId The model run's ID.
     * @param gdalRaster The mean prediction raster, in any GDAL format supported by the PostGIS database.
     */
    @Override
    public void updateMeanPredictionRasterForModelRun(int modelRunId, byte[] gdalRaster) {
        nativeSQL.updateRasterForModelRun(modelRunId, gdalRaster, NativeSQLImpl.MEAN_PREDICTION_RASTER_COLUMN_NAME);
    }

    /**
     * Updates the specified model run to include the specified prediction uncertainty raster.
     * @param modelRunId The model run's ID.
     * @param gdalRaster The prediction uncertainty raster, in any GDAL format supported by the PostGIS database.
     */
    @Override
    public void updatePredictionUncertaintyRasterForModelRun(int modelRunId, byte[] gdalRaster) {
        nativeSQL.updateRasterForModelRun(modelRunId, gdalRaster,
                NativeSQLImpl.PREDICTION_UNCERTAINTY_RASTER_COLUMN_NAME);
    }
}
