package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.domain.HealthMapAlert;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the HealthMapAlertValidator class.

 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapAlertValidatorTest {
    @Test
    public void alertWithNoDiseaseIdsIsInvalid() {
        HealthMapAlert alert = createHealthMapAlert(5);
        alert.setDiseases(Arrays.asList("Disease 1", "Disease 2"));
        actAndAssertMessage(alert, "Missing disease IDs in HealthMap alert (alert ID 5)");
    }

    @Test
    public void alertWithNoDiseaseNamesIsInvalid() {
        HealthMapAlert alert = createHealthMapAlert(5);
        alert.setDiseaseIds(Arrays.asList("123", "456"));
        actAndAssertMessage(alert, "Missing diseases in HealthMap alert (alert ID 5)");
    }

    @Test
    public void alertWithDiseaseIdCountNotEqualToDiseaseNameCountIsInvalid() {
        HealthMapAlert alert = createHealthMapAlert(5);
        alert.setDiseaseIds(Arrays.asList("123", "456"));
        alert.setDiseases(Arrays.asList("Disease 1", "Disease 2", "Disease 3"));
        actAndAssertMessage(alert, "HealthMap alert has 2 disease ID(s) but 3 disease name(s) (alert ID 5)");
    }

    private HealthMapAlert createHealthMapAlert(int alertId) {
        HealthMapAlert alert = new HealthMapAlert();
        alert.setLink("http://healthmap.org/ln.php?" + alertId);
        return alert;
    }

    private void actAndAssertMessage(HealthMapAlert alert, String expectedMessage) {
        HealthMapAlertValidator validator = new HealthMapAlertValidator(alert);
        String message = validator.validate();
        assertThat(message).isEqualTo(expectedMessage);
    }
}
