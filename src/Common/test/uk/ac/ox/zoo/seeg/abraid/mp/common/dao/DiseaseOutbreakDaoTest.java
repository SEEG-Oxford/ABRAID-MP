package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.Calendar;
import java.util.Date;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the DiseaseOutbreakDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOutbreakDaoTest extends AbstractSpringIntegrationTests {
    @Autowired
    private DiseaseOutbreakDao diseaseOutbreakDao;
    @Autowired
    private CountryDao countryDao;
    @Autowired
    private ProvenanceDao provenanceDao;
    @Autowired
    private DiseaseDao diseaseDao;
    @Autowired
    private LocationDao locationDao;

    @Test
    public void saveAndReloadDiseaseOutbreakWithExistingParents() {
        String countryName = "Australia";
        String provenanceName = "Google News";
        String diseaseName = "Malaria";
        String title = "News title";

        // Retrieve existing country, provenance and disease
        Country country = countryDao.getByName(countryName);
        Provenance provenance = provenanceDao.getByName(provenanceName);
        Disease disease = diseaseDao.getByName(diseaseName);
        Integer provenanceId = provenance.getId();
        Integer diseaseId = disease.getId();

        // Create dates
        Date publicationDate = Calendar.getInstance().getTime();
        Calendar outbreakStartCalendar = Calendar.getInstance();
        outbreakStartCalendar.add(Calendar.HOUR, -3);
        Date outbreakStartDate = outbreakStartCalendar.getTime();

        // Create and save location
        Location location = new Location();
        location.setCountry(country);
        locationDao.save(location);
        Integer locationId = location.getId();
        flushAndClear();

        DiseaseOutbreak outbreak = new DiseaseOutbreak();
        outbreak.setDisease(disease);
        outbreak.setLocation(location);
        outbreak.setProvenance(provenance);
        outbreak.setTitle(title);
        outbreak.setPublicationDate(publicationDate);
        outbreak.setOutbreakStartDate(outbreakStartDate);

        diseaseOutbreakDao.save(outbreak);
        Integer diseaseOutbreakId = outbreak.getId();
        flushAndClear();

        // Reload the same disease outbreak and verify its properties (and its parents' properties)
        outbreak = diseaseOutbreakDao.getById(diseaseOutbreakId);
        assertThat(outbreak).isNotNull();
        assertThat(outbreak.getLocation()).isNotNull();
        assertThat(outbreak.getLocation().getId()).isEqualTo(locationId);
        assertThat(outbreak.getLocation().getCountry()).isNotNull();
        assertThat(outbreak.getLocation().getCountry().getName()).isEqualTo(countryName);
        assertThat(outbreak.getDisease().getId()).isEqualTo(diseaseId);
        assertThat(outbreak.getDisease()).isNotNull();
        assertThat(outbreak.getDisease().getName()).isEqualTo(diseaseName);
        assertThat(outbreak.getProvenance()).isNotNull();
        assertThat(outbreak.getProvenance().getId()).isEqualTo(provenanceId);
        assertThat(outbreak.getProvenance().getName()).isEqualTo(provenanceName);
        assertThat(outbreak.getTitle()).isEqualTo(title);
        assertThat(outbreak.getPublicationDate()).isEqualTo(publicationDate);
        assertThat(outbreak.getOutbreakStartDate()).isEqualTo(outbreakStartDate);
    }

    @Test
    public void saveAndReloadDiseaseOutbreakWithNewParents() {
        String countryName = "Kenya";
        String provenanceName = "My New Provenance";
        String diseaseName = "My New Disease";
        String title = "News title";

        // Retrieve existing country
        Country country = countryDao.getByName(countryName);

        // Create provenance, disease and location
        Provenance provenance = new Provenance(provenanceName);
        Location location = new Location(country);
        Disease disease = new Disease(diseaseName);

        // Create dates
        Date publicationDate = Calendar.getInstance().getTime();
        Calendar outbreakStartCalendar = Calendar.getInstance();
        outbreakStartCalendar.add(Calendar.HOUR, -3);
        Date outbreakStartDate = outbreakStartCalendar.getTime();

        DiseaseOutbreak outbreak = new DiseaseOutbreak();
        outbreak.setDisease(disease);
        outbreak.setLocation(location);
        outbreak.setProvenance(provenance);
        outbreak.setTitle(title);
        outbreak.setPublicationDate(publicationDate);
        outbreak.setOutbreakStartDate(outbreakStartDate);

        diseaseOutbreakDao.save(outbreak);
        Integer diseaseOutbreakId = outbreak.getId();
        flushAndClear();

        // Reload the same disease outbreak and verifies its properties
        outbreak = diseaseOutbreakDao.getById(diseaseOutbreakId);
        assertThat(outbreak).isNotNull();
        assertThat(outbreak.getLocation()).isNotNull();
        assertThat(outbreak.getLocation().getCountry()).isNotNull();
        assertThat(outbreak.getLocation().getCountry().getName()).isEqualTo(countryName);
        assertThat(outbreak.getDisease()).isNotNull();
        assertThat(outbreak.getDisease().getName()).isEqualTo(diseaseName);
        assertThat(outbreak.getProvenance()).isNotNull();
        assertThat(outbreak.getProvenance().getName()).isEqualTo(provenanceName);
        assertThat(outbreak.getTitle()).isEqualTo(title);
        assertThat(outbreak.getPublicationDate()).isEqualTo(publicationDate);
        assertThat(outbreak.getOutbreakStartDate()).isEqualTo(outbreakStartDate);
    }

    @Test
    public void loadNonExistentDisease() {
        String diseaseName = "This disease does not exist";
        Disease disease = diseaseDao.getByName(diseaseName);
        assertThat(disease).isNull();
    }
}
