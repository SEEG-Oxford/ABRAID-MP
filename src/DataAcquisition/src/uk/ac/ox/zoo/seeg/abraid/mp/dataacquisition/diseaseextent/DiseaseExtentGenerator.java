package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.diseaseextent;

/**
 * Generates disease extents for all relevant diseases.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseExtentGenerator {
    private SingleDiseaseExtentGenerator singleDiseaseExtentGenerator;

    public DiseaseExtentGenerator(SingleDiseaseExtentGenerator singleDiseaseExtentGenerator) {
        this.singleDiseaseExtentGenerator = singleDiseaseExtentGenerator;
    }

    /**
     * Generates disease extents for all relevant diseases.
     */
    public void generateDiseaseExtent() {
        // Temporary: Generates the disease extent for dengue (disease group ID = 87), using hardcoded parameters

        ///CHECKSTYLE:OFF MagicNumberCheck - this hardcoding will be removed when system administration is in place
        DiseaseExtentParameters parameters = new DiseaseExtentParameters(null, 5, 0.6, 5, 1);
        singleDiseaseExtentGenerator.generateDiseaseExtent(87, parameters);
        ///CHECKSTYLE:ON MagicNumberCheck
    }
}
