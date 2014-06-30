package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.csv;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for CsvCovariateInfluence.
 * Copyright (c) 2014 University of Oxford
 */
public class CsvCovariateInfluenceTest {
    @Test
    public void readFromCSVReturnsCorrectResult() throws IOException {
        // Arrange
        String csv =
                "\"\",\"mean\",\"2.5%\",\"97.5%\"\n" +
                "\"rand2\",38.3298990317527,37.2171654834767,39.4426325800287\n" +
                "\"rand3\",35.7954256696217,35.0476600405772,36.5431912986662\n" +
                "\"rand1\",25.8746752986256,24.0141761213051,27.7351744759462\n";

        // Act
        List<CsvCovariateInfluence> result = CsvCovariateInfluence.readFromCSV(csv);

        // Assert
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getCovariateName()).isEqualTo("rand2");
        assertThat(result.get(0).getMeanInfluence()).isEqualTo(38.3298990317527);
        assertThat(result.get(0).getLowerQuantile()).isEqualTo(37.2171654834767);
        assertThat(result.get(0).getUpperQuantile()).isEqualTo(39.4426325800287);
        assertThat(result.get(1).getCovariateName()).isEqualTo("rand3");
    }
}
