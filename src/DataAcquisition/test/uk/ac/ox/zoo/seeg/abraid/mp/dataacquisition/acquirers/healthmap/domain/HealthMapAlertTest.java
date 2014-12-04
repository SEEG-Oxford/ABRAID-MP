package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.domain;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Test
    public void nullDiseaseIdsAndNoDiseaseIdReturnsEmptyList() {
        HealthMapAlert alert = createHealthMapAlertWithDiseaseIds(null, null);
        assertThat(alert.getDiseaseIds()).isEmpty();
    }

    @Test
    public void noDiseaseIdsReturnsDiseaseId() {
        HealthMapAlert alert = createHealthMapAlertWithDiseaseIds("123", new ArrayList<String>());
        assertThat(alert.getDiseaseIds()).hasSize(1);
        assertThat(alert.getDiseaseIds()).contains(123);
    }

    @Test
    public void nullDiseaseIdsReturnsDiseaseId() {
        HealthMapAlert alert = createHealthMapAlertWithDiseaseIds("123", null);
        assertThat(alert.getDiseaseIds()).hasSize(1);
        assertThat(alert.getDiseaseIds()).contains(123);
    }

    @Test
    public void diseaseIdsButNoDiseaseIdReturnsDiseaseIds() {
        HealthMapAlert alert = createHealthMapAlertWithDiseaseIds(null, Arrays.asList("123", "456"));
        assertThat(alert.getDiseaseIds()).hasSize(2);
        assertThat(alert.getDiseaseIds()).contains(123, 456);
    }

    @Test
    public void diseaseIdsAndDiseaseIdReturnsDiseases() {
        HealthMapAlert alert = createHealthMapAlertWithDiseaseIds("789", Arrays.asList("123", "456"));
        assertThat(alert.getDiseaseIds()).hasSize(2);
        assertThat(alert.getDiseaseIds()).contains(123, 456);
    }

    @Test
    public void setDiseaseIdsOnlyAddsSuccessfullyParsedDiseasesToTheList() {
        HealthMapAlert alert = createHealthMapAlertWithDiseaseIds(null, Arrays.asList("123", "some text", "456"));
        assertThat(alert.getDiseaseIds()).hasSize(2);
        assertThat(alert.getDiseaseIds()).contains(123, 456);
    }

    @Test
    public void nullDiseasesAndNoDiseaseReturnsEmptyList() {
        HealthMapAlert alert = createHealthMapAlertWithDiseases(null, null);
        assertThat(alert.getDiseases()).isEmpty();
    }

    @Test
    public void noDiseasesReturnsDisease() {
        HealthMapAlert alert = createHealthMapAlertWithDiseases("Disease 1", new ArrayList<String>());
        assertThat(alert.getDiseases()).hasSize(1);
        assertThat(alert.getDiseases()).contains("Disease 1");
    }

    @Test
    public void nullDiseasesReturnsDisease() {
        HealthMapAlert alert = createHealthMapAlertWithDiseases("Disease 1", null);
        assertThat(alert.getDiseases()).hasSize(1);
        assertThat(alert.getDiseases()).contains("Disease 1");
    }

    @Test
    public void diseasesButNoDiseaseReturnsDiseases() {
        HealthMapAlert alert = createHealthMapAlertWithDiseases(null, Arrays.asList("Disease 1", "Disease 2"));
        assertThat(alert.getDiseases()).hasSize(2);
        assertThat(alert.getDiseases()).contains("Disease 1", "Disease 2");
    }

    @Test
    public void diseasesAndDiseaseReturnsDiseases() {
        HealthMapAlert alert = createHealthMapAlertWithDiseases("Disease 3", Arrays.asList("Disease 1", "Disease 2"));
        assertThat(alert.getDiseases()).hasSize(2);
        assertThat(alert.getDiseases()).contains("Disease 1", "Disease 2");
    }

    @Test
    public void splitCommentReturnsEmptyListForANullComment() {
        HealthMapAlert alert = createHealthMapAlertWithComment(null);
        assertThat(alert.getSplitComment()).hasSize(0);
    }

    @Test
    public void splitCommentReturnsEmptyListForAWhitespaceComment() {
        HealthMapAlert alert = createHealthMapAlertWithComment("    ");
        assertThat(alert.getSplitComment()).hasSize(0);
    }

    @Test
    public void splitCommentReturnsOneListItemForOneSubdisease() {
        HealthMapAlert alert = createHealthMapAlertWithComment("  pf  ");
        assertThat(alert.getSplitComment()).hasSize(1);
        assertThat(alert.getSplitComment().get(0)).isEqualTo("pf");
    }

    @Test
    public void splitCommentReturnsTwoListItemsForTwoSubdiseasesWithEmptyTokenAndWhitespaceAndCapitals() {
        HealthMapAlert alert = createHealthMapAlertWithComment("P f, , p V");
        assertThat(alert.getSplitComment()).hasSize(2);
        assertThat(alert.getSplitComment().get(0)).isEqualTo("pf");
        assertThat(alert.getSplitComment().get(1)).isEqualTo("pv");
    }

    private HealthMapAlert createHealthMapAlert(String link) {
        HealthMapAlert alert = new HealthMapAlert();
        alert.setLink(link);
        return alert;
    }

    private HealthMapAlert createHealthMapAlertWithDiseaseIds(String diseaseId, List<String> diseaseIds) {
        HealthMapAlert alert = new HealthMapAlert();
        alert.setDiseaseId(diseaseId);
        alert.setDiseaseIds(diseaseIds);
        return alert;
    }

    private HealthMapAlert createHealthMapAlertWithDiseases(String disease, List<String> diseases) {
        HealthMapAlert alert = new HealthMapAlert();
        alert.setDisease(disease);
        alert.setDiseases(diseases);
        return alert;
    }

    private HealthMapAlert createHealthMapAlertWithComment(String comment) {
        HealthMapAlert alert = new HealthMapAlert();
        alert.setComment(comment);
        return alert;
    }
}
