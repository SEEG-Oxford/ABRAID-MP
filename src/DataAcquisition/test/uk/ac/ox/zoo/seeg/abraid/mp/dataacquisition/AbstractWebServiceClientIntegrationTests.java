package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition;

import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.kubek2k.springockito.annotations.WrapWithSpy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClient;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.ModelWrapperWebService;

/**
 * Base class for Main class tests in the Data Acquisition module.
 *
 * Copyright (c) 2014 University of Oxford
 */
@ContextConfiguration(loader = SpringockitoContextLoader.class,
                      locations = "classpath:uk/ac/ox/zoo/seeg/abraid/mp/dataacquisition/config/beans.xml")
public abstract class AbstractWebServiceClientIntegrationTests extends AbstractDataAcquisitionSpringIntegrationTests {
    @ReplaceWithMock
    @Autowired
    protected WebServiceClient webServiceClient;

    @WrapWithSpy
    @Autowired
    protected ModelWrapperWebService modelWrapperWebService;
}
