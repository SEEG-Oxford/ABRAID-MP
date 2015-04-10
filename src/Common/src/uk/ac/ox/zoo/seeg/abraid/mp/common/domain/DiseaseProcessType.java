package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

/**
 * The type of disease process.
 * Copyright (c) 2015 University of Oxford
 */
public enum DiseaseProcessType {
    /**
     * An automatically triggered process, using all data.
     */
    AUTOMATIC,
    /**
     * A manually triggered process, using all data.
     */
    MANUAL,
    /**
     * A manually triggered process, using only gold standard data.
     */
    MANUAL_GOLD_STANDARD
}
