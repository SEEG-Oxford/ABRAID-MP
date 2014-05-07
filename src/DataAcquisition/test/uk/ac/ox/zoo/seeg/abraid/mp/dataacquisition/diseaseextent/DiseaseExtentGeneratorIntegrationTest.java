package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.diseaseextent;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.AbstractDataAcquisitionSpringIntegrationTests;

/**
 * Contains integration tests for the DiseaseExtentGenerator class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseExtentGeneratorIntegrationTest extends AbstractDataAcquisitionSpringIntegrationTests {
    @Autowired
    private DiseaseExtentGenerator diseaseExtentGenerator;

    @Test
    public void generateDiseaseExtent() {
        executeQuery("delete from AdminUnitDiseaseExtentClass");
        diseaseExtentGenerator.generateDiseaseExtent();
    }
}
