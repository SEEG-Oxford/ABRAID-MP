package uk.ac.ox.zoo.seeg.abraid.mp.testutils;

import org.joda.time.DateTime;

import static org.mockito.Matchers.argThat;

/**
 * Matchers for use with Mockito.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class Matchers {
    public static DateTime approx(DateTime date) {
        return argThat(new ApproximatelyMatches(date));
    }
}
