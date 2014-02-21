package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition;

import org.junit.Test;

/**
 * Tests the Main class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class MainTest {
    @Test
    public void mainMethod() {
        // TODO: Ensure that this rolls back, and that the WebServiceClient and database is mocked out using
        // Springokito.
        Main.main(new String[]{});
    }
}
