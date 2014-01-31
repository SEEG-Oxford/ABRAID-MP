package uk.ac.ox.zoo.seeg.abraid.mp.common;

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
    protected CountryDao countryDao;

    @ReplaceWithMock
    @Autowired
    protected DiseaseDao diseaseDao;

    @ReplaceWithMock
    @Autowired
    protected DiseaseOutbreakDao diseaseOutbreakDao;

    @ReplaceWithMock
    @Autowired
    protected LocationDao locationDao;

    @ReplaceWithMock
    @Autowired
    protected ProvenanceDao provenanceDao;

    @ReplaceWithMock
    @Autowired
    protected ProvenanceWeightDao provenanceWeightDao;
}
