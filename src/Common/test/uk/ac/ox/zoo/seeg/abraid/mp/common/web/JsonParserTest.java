package uk.ac.ox.zoo.seeg.abraid.mp.common.web;

import com.fasterxml.jackson.core.type.TypeReference;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
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
        String json = "{ \"name\": \"Boris Becker\", \"age\": 46, \"dateOfBirth\": \"1967-11-22\" }";
        JsonParser parser = new JsonParser();

        // Act
        JsonParserTestPerson person = parser.parse(json, JsonParserTestPerson.class);

        // Assert
        assertThat(person.getName()).isEqualTo("Boris Becker");
        assertThat(person.getAge()).isEqualTo(46);
        DateTime expected = new DateTime("1967-11-22");
        assertThat(person.getDateOfBirth().getMillis()).isEqualTo(expected.getMillis());
    }

    @Test
    public void parseInvalidDataType() {
        // Arrange
        String json = "{ \"name\": \"Boris Becker\", \"age\": \"Invalid\" }";
        JsonParser parser = new JsonParser();

        // Act
        catchException(parser).parse(json, JsonParserTestPerson.class);

        // Assert
        assertThat(caughtException()).isInstanceOf(JsonParserException.class);
    }

    @Test
    public void parseInvalidJsonFormat() {
        // Arrange
        String json = "{ \"name\" Boris Becker, \"age\" 46 }";
        JsonParser parser = new JsonParser();

        // Act
        catchException(parser).parse(json, JsonParserTestPerson.class);

        // Assert
        assertThat(caughtException()).isInstanceOf(JsonParserException.class);
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

    @Test
    public void parseIntoTypeReferenceInvalidJsonFormat() {
        // Arrange
        String json = "[ { \"name\": \"Boris Becker\", \"age\" 46 }, { \"name\": \"Pete Sampras\", \"age\": 42 } ]";
        JsonParser parser = new JsonParser();

        // Act
        catchException(parser).parse(json, new TypeReference<List<JsonParserTestPerson>>() { });

        // Assert
        assertThat(caughtException()).isInstanceOf(JsonParserException.class);
    }

    @Test
    public void parseUsingISO8601DateFormat() {
        // Arrange
        String json = "{ \"name\": \"Boris Becker\", \"age\": 46, \"dateOfBirth\": \"1967-11-22T11:22:33+0400\" }";
        JsonParser parser = new JsonParser();

        // Act
        JsonParserTestPerson person = parser.parse(json, JsonParserTestPerson.class);

        // Assert
        assertThat(person.getName()).isEqualTo("Boris Becker");
        assertThat(person.getAge()).isEqualTo(46);
        DateTime expected = new DateTime("1967-11-22T11:22:33+0400");
        assertThat(person.getDateOfBirth().getMillis()).isEqualTo(expected.getMillis());
    }

    @Test
    public void parseUsingSpecifiedDateFormat() {
        // Arrange
        String json = "{ \"name\": \"Boris Becker\", \"age\": 46, \"dateOfBirth\": \"1967-11-22 11:22:33+0400\" }";
        final String dateTimeFormatString = "yyyy-MM-dd HH:mm:ssZ";
        JsonParser parser = new JsonParser(DateTimeFormat.forPattern(dateTimeFormatString));

        // Act
        JsonParserTestPerson person = parser.parse(json, JsonParserTestPerson.class);

        // Assert
        assertThat(person.getName()).isEqualTo("Boris Becker");
        assertThat(person.getAge()).isEqualTo(46);
        DateTime expected = new DateTime("1967-11-22T11:22:33+0400");
        assertThat(person.getDateOfBirth().getMillis()).isEqualTo(expected.getMillis());
    }
}
