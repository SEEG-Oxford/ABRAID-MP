package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.postqc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.Main;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc.PostQCManager;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Performs integration tests for the PostQCManager class.
 *
 * Copyright (c) 2014 University of Oxford
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = Main.APPLICATION_CONTEXT_LOCATION)
@Transactional
public class PostQCManagerIntegrationTest {
    @Autowired
    private PostQCManager postQCManager;

    @Test
    public void findsAdminUnitsIfExactlyOnGeometry() {
        Location location = new Location(172, -42);
        postQCManager.runPostQCProcesses(location);
        assertThat(location.getAdminUnitGlobalGaulCode()).isEqualTo(179);
        assertThat(location.getAdminUnitTropicalGaulCode()).isEqualTo(179);
    }

    @Test
    public void findsAdminUnitsIfWithinGeometry() {
        Location location = new Location(101.7, 3.16667);
        postQCManager.runPostQCProcesses(location);
        assertThat(location.getAdminUnitGlobalGaulCode()).isEqualTo(153);
        assertThat(location.getAdminUnitTropicalGaulCode()).isEqualTo(153);
    }

    @Test
    public void findsAdminUnitsIfWithinGeometryWithDifferentGaulCodesForGlobalAndTropical() {
        Location britishColumbiaCanadaCentroid = new Location(-124.76033, 54.75946);
        postQCManager.runPostQCProcesses(britishColumbiaCanadaCentroid);
        assertThat(britishColumbiaCanadaCentroid.getAdminUnitGlobalGaulCode()).isEqualTo(826);
        assertThat(britishColumbiaCanadaCentroid.getAdminUnitTropicalGaulCode()).isEqualTo(825);
    }

    @Test
    public void findsAdminUnitsIfOutsideGeometry() {
        Location location = new Location(40, 50);
        postQCManager.runPostQCProcesses(location);
        assertThat(location.getAdminUnitGlobalGaulCode()).isNull();
        assertThat(location.getAdminUnitTropicalGaulCode()).isNull();
    }
}
