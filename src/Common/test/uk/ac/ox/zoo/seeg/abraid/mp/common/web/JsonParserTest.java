package uk.ac.ox.zoo.seeg.abraid.mp.common.web;

import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the JSON parser.
 * As we are using an established library (Jackson), the parsing itself needs minimal testing.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class JsonParserTest {
    @Test
    public void parseSuccessful() throws IOException {
        // Arrange
        String json = "{ \"name\": \"Boris Becker\", \"age\": 46 }";
        JsonParser parser = new JsonParser();

        // Act
        JsonParserTestPerson person = parser.parse(json, JsonParserTestPerson.class);

        // Assert
        assertThat(person.getName()).isEqualTo("Boris Becker");
        assertThat(person.getAge()).isEqualTo(46);
    }

    @Test(expected = JsonParserException.class)
    public void parseInvalidDataType() {
        // Arrange
        String json = "{ \"name\": \"Boris Becker\", \"age\": \"Invalid\" }";
        JsonParser parser = new JsonParser();

        // Act
        parser.parse(json, JsonParserTestPerson.class);

        // Assert: see annotation for expected exception
    }

    @Test(expected = JsonParserException.class)
    public void parseInvalidJsonFormat() {
        // Arrange
        String json = "{ \"name\" Boris Becker, \"age\" 46 }";
        JsonParser parser = new JsonParser();

        // Act
        parser.parse(json, JsonParserTestPerson.class);

        // Assert: see annotation for expected exception
    }

    @Test
    public void parseIntoTypeReference() {
        // Arrange
        String json = "[ { \"name\": \"Boris Becker\", \"age\": 46 }, { \"name\": \"Pete Sampras\", \"age\": 42 } ]";
        JsonParser parser = new JsonParser();

        // Act
        List<JsonParserTestPerson> people = parser.parse(json, new TypeReference<List<JsonParserTestPerson>>() { });

        // Assert
        assertThat(people).hasSize(2);
        assertThat(people.get(0).getName()).isEqualTo("Boris Becker");
        assertThat(people.get(0).getAge()).isEqualTo(46);
        assertThat(people.get(1).getName()).isEqualTo("Pete Sampras");
        assertThat(people.get(1).getAge()).isEqualTo(42);
    }
}
