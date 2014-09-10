package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the HealthMapAlert class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapAlertTest {
    @Test
    public void validLinkGivesAlertId() {
        HealthMapAlert alert = createHealthMapAlert("http://healthmap.org/ln.php?2177808");
        assertThat(alert.getAlertId()).isEqualTo(2177808);
    }

    @Test
    public void validExtendedLinkGivesAlertId() {
        HealthMapAlert alert = createHealthMapAlert("http://healthmap.org/ln.php?2224258&trto=en&trfr=fr");
        assertThat(alert.getAlertId()).isEqualTo(2224258);
    }

    @Test
    public void invalidLinkGivesNullAlertId() {
        HealthMapAlert alert = createHealthMapAlert("http://www.google.co.uk");
        assertThat(alert.getAlertId()).isNull();
    }

    @Test
    public void missingLinkGivesNullAlertId() {
        HealthMapAlert alert = new HealthMapAlert();
        assertThat(alert.getAlertId()).isNull();
    }

    public HealthMapAlert createHealthMapAlert(String link) {
        HealthMapAlert alert = new HealthMapAlert();
        alert.setLink(link);
        return alert;
    }
}
