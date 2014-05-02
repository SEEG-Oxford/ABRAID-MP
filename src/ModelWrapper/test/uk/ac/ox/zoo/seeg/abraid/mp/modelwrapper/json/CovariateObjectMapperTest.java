package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests for CovariateObjectMapper
 * Copyright (c) 2014 University of Oxford
 */
public class CovariateObjectMapperTest {
    private static String JSON =
            "{\n" +
            "  \"diseases\" : [ {\n" +
            "    \"id\" : 22,\n" +
            "    \"name\" : \"Ascariasis\"\n" +
            "  }, {\n" +
            "    \"id\" : 64,\n" +
            "    \"name\" : \"Cholera\"\n" +
            "  } ],\n" +
            "  \"files\" : [ {\n" +
            "    \"path\" : \"f1\",\n" +
            "    \"name\" : \"a\",\n" +
            "    \"info\" : null,\n" +
            "    \"hide\" : false,\n" +
            "    \"enabled\" : [ 22 ]\n" +
            "  }, {\n" +
            "    \"path\" : \"f2\",\n" +
            "    \"name\" : \"\",\n" +
            "    \"info\" : null,\n" +
            "    \"hide\" : true,\n" +
            "    \"enabled\" : [ ]\n" +
            "  }]\n" +
            "}";

    @Test
    public void mapperCanDeserialize() throws IOException {
        // Arrange
        ObjectMapper target = new CovariateObjectMapper();

        // Act
        JsonCovariateConfiguration result = target.readValue(JSON, JsonCovariateConfiguration.class);

        // Assert
        assertThat(result.isValid()).isEqualTo(true);
        assertThat(result.getDiseases().size()).isEqualTo(2);
        assertThat(result.getFiles().size()).isEqualTo(2);
    }

    @Test
    public void mapperCanSerialize() throws IOException {
        // Arrange
        ObjectMapper target = new CovariateObjectMapper();
        JsonCovariateConfiguration conf = new JsonCovariateConfiguration();
        conf.setDiseases(Arrays.asList(
                new JsonDisease(22, "Ascariasis"),
                new JsonDisease(64, "Cholera")
        ));
        conf.setFiles(Arrays.asList(
                new JsonCovariateFile("f1", "a", null, false, Arrays.asList(22)),
                new JsonCovariateFile("f2", "", null, true, new ArrayList<Integer>())
        ));

        // Act
        String result = target.writeValueAsString(conf);

        // Assert
        assertThat(result).isEqualTo(JSON.replace(" ", "").replace("\n", ""));
    }
}

