package uk.ac.ox.zoo.seeg.abraid.mp.common;

import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.*;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractSpringIntegrationTests;

/**
 * Base class for Spring-enabled unit tests. Mocks out the Data Access Objects.
 *
 * Copyright (c) 2014 University of Oxford
 */
@ContextConfiguration(loader = SpringockitoContextLoader.class,
                      locations = "classpath:uk/ac/ox/zoo/seeg/abraid/mp/common/config/beans.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractCommonSpringUnitTests extends AbstractSpringIntegrationTests {
    @ReplaceWithMock
    @Autowired
    protected AdminUnitDiseaseExtentClassDao adminUnitDiseaseExtentClassDao;

    @ReplaceWithMock
    @Autowired
    protected AdminUnitGlobalDao adminUnitGlobalDao;

    @ReplaceWithMock
    @Autowired
    protected AdminUnitReviewDao adminUnitReviewDao;

    @ReplaceWithMock
    @Autowired
    protected AdminUnitTropicalDao adminUnitTropicalDao;

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
    protected DiseaseExtentClassDao diseaseExtentClassDao;

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
    protected ModelRunDao modelRunDao;

    @ReplaceWithMock
    @Autowired
    protected NativeSQL nativeSQL;

    @ReplaceWithMock
    @Autowired
    protected ProvenanceDao provenanceDao;

    @ReplaceWithMock
    @Autowired
    protected ValidatorDiseaseGroupDao validatorDiseaseGroupDao;
}
