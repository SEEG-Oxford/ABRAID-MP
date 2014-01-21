package uk.ac.ox.zoo.seeg.abraid.mp.common;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.fest.assertions.api.Assertions.*;

public class ExampleTests {
    @Test
    public void one_is_equal_to_one() {
        assertThat(1).isEqualTo(1).isPositive().isNotZero().isLessThan(2);
    }
}
