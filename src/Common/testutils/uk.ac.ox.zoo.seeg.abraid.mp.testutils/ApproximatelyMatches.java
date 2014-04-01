package uk.ac.ox.zoo.seeg.abraid.mp.testutils;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.mockito.ArgumentMatcher;

/**
 * Matcher that compares two dates for approximate equality, for use with dates that rely on the current time.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ApproximatelyMatches extends ArgumentMatcher<DateTime> {
    private DateTime comparisonDate;
    private static final long TOLERANCE_MILLISECONDS = 1000;

    public ApproximatelyMatches(DateTime comparisonDate) {
        this.comparisonDate = comparisonDate;
    }

    /**
     * True if the date matches the comparison date, otherwise false.
     * @param date The date.
     * @return True if the date matches the comparison date, otherwise false.
     */
    public boolean matches(Object date) {
        if (comparisonDate == null && date == null) {
            return true;
        }

        if (comparisonDate == null || date == null) {
            return false;
        }

        DateTime dateTime = (DateTime) date;
        Duration duration;
        if (comparisonDate.isBefore(dateTime)) {
            duration = new Duration(comparisonDate, dateTime);
        } else {
            duration = new Duration(dateTime, comparisonDate);
        }

        return duration.getMillis() < TOLERANCE_MILLISECONDS;
    }
}
