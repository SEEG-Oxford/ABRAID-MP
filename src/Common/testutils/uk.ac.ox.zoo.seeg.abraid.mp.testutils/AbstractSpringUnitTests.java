package uk.ac.ox.zoo.seeg.abraid.mp.testutils;

import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.*;

/**
 * Base class for Spring-enabled unit tests. Mocks out the Data Access Objects.
 *
 * Copyright (c) 2014 University of Oxford
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class,
                      locations = "classpath:uk/ac/ox/zoo/seeg/abraid/mp/common/config/beans.xml")
public abstract class AbstractSpringUnitTests {
    @ReplaceWithMock
    @Autowired
    protected AdminUnitQCDao adminUnitQCDao;

    @ReplaceWithMock
    @Autowired
    protected AlertDao alertDao;

    @ReplaceWithMock
    @Autowired
    protected CountryDao countryDao;

    @ReplaceWithMock
    @Autowired
    protected DiseaseGroupDao diseaseGroupDao;

    @ReplaceWithMock
    @Autowired
    protected DiseaseOccurrenceDao diseaseOccurrenceDao;

    @ReplaceWithMock
    @Autowired
    protected DiseaseOccurrenceReviewDao diseaseOccurrenceReviewDao;

    @ReplaceWithMock
    @Autowired
    protected ExpertDao expertDao;

    @ReplaceWithMock
    @Autowired
    protected FeedDao feedDao;

    @ReplaceWithMock
    @Autowired
    protected GeoNamesLocationPrecisionDao geoNamesLocationPrecisionDao;

    @ReplaceWithMock
    @Autowired
    protected HealthMapCountryDao healthMapCountryDao;

    @ReplaceWithMock
    @Autowired
    protected HealthMapDiseaseDao healthMapDiseaseDao;

    @ReplaceWithMock
    @Autowired
    protected LandSeaBorderDao landSeaBorderDao;

    @ReplaceWithMock
    @Autowired
    protected LocationDao locationDao;

    @ReplaceWithMock
    @Autowired
    protected ProvenanceDao provenanceDao;

    @ReplaceWithMock
    @Autowired
    protected ValidatorDiseaseGroupDao validatorDiseaseGroupDao;
}
