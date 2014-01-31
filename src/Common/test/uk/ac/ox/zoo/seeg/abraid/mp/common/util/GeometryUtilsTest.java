package uk.ac.ox.zoo.seeg.abraid.mp.common.util;

import com.vividsolutions.jts.geom.Point;
import org.junit.Test;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the GeometryUtils class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class GeometryUtilsTest {
    @Test
    public void createPointHasCorrectPrecisionAndSRID() {
        Point point = GeometryUtils.createPoint(-1.2824895,51.6743376);
        assertThat(point.getX()).isEqualTo(-1.28249);
        assertThat(point.getY()).isEqualTo(51.67434);
        assertThat(point.getSRID()).isEqualTo(4326);
    }
}
