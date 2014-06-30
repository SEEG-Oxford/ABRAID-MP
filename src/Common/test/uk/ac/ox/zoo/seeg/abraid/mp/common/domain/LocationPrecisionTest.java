package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the LocationPrecision class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class LocationPrecisionTest {
    @Test
    public void findByHealthMapPlaceBasicType() {
        assertThat(LocationPrecision.findByHealthMapPlaceBasicType("c")).isEqualTo(LocationPrecision.COUNTRY);
        assertThat(LocationPrecision.findByHealthMapPlaceBasicType("l")).isEqualTo(LocationPrecision.ADMIN1);
        assertThat(LocationPrecision.findByHealthMapPlaceBasicType("p")).isEqualTo(LocationPrecision.PRECISE);
        assertThat(LocationPrecision.findByHealthMapPlaceBasicType("x")).isNull();
    }
}
