package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.runrequest.data;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonModellingDiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.ModellingLocationPrecisionAdjuster;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides a mechanism for writing model input occurrence data into the working directory.
 * Copyright (c) 2014 University of Oxford
 */
public class OccurrenceDataWriterImpl implements OccurrenceDataWriter {
    private static final Logger LOGGER = Logger.getLogger(OccurrenceDataWriterImpl.class);
    private static final String LOG_WRITING_OCCURRENCE_DATA = "Writing %d occurrence data points to workspace at %s";
    private ModellingLocationPrecisionAdjuster modellingLocationPrecisionAdjuster;

    public OccurrenceDataWriterImpl(ModellingLocationPrecisionAdjuster modellingLocationPrecisionAdjuster) {
        this.modellingLocationPrecisionAdjuster = modellingLocationPrecisionAdjuster;
    }

    /**
     * Write the occurrence data to a csv file ready to run the model.
     * @param occurrenceData The data to be written.
     * @param targetFile The file to be created.
     * @throws IOException If the data could not be written.
     */
    @Override
    public void write(List<DiseaseOccurrence> occurrenceData, File targetFile)
            throws IOException {
        LOGGER.info(String.format(
                LOG_WRITING_OCCURRENCE_DATA, occurrenceData.size(), targetFile.toString()));

        List<JsonModellingDiseaseOccurrence> occurrences = new ArrayList<>();
        for (DiseaseOccurrence occurrence : occurrenceData) {
            occurrences.add(new JsonModellingDiseaseOccurrence(modellingLocationPrecisionAdjuster, occurrence));
        }

        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = csvMapper.schemaFor(JsonModellingDiseaseOccurrence.class).withHeader();
        try (FileOutputStream fileStream = new FileOutputStream(targetFile.getAbsoluteFile())) {
            csvMapper.writer(schema).writeValue(fileStream, occurrences);
            fileStream.flush();
        }
    }
}
