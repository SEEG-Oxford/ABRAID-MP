package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.views.support;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import org.junit.Test;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.WrappedList;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for CSVMessageConverter.
 * Copyright (c) 2014 University of Oxford
 */
public class CSVMessageConverterTest {
    @Test
    public void canWriteReturnsFalseForUnsuitableJavaTypes() throws Exception {
        // Arrange
        CSVMessageConverter target = new CSVMessageConverter(mock(CsvMapper.class));

        // Act / Assert
        assertThat(target.canWrite(List.class, MediaType.parseMediaType("application/csv"))).isFalse();
        assertThat(target.canWrite(String.class, MediaType.parseMediaType("application/csv"))).isFalse();
        assertThat(target.canWrite(Object.class, MediaType.parseMediaType("application/csv"))).isFalse();
        assertThat(target.canWrite(CSVMessageConverter.class, MediaType.parseMediaType("application/csv"))).isFalse();
    }

    @Test
    public void canWriteReturnsFalseForUnsuitableMediaTypes() throws Exception {
        // Arrange
        CSVMessageConverter target = new CSVMessageConverter(mock(CsvMapper.class));

        // Act / Assert
        assertThat(target.canWrite(WrappedList.class, MediaType.parseMediaType("application/json"))).isFalse();
        assertThat(target.canWrite(WrappedList.class, MediaType.parseMediaType("text/csv"))).isFalse();
        assertThat(target.canWrite(WrappedList.class, MediaType.parseMediaType("text/html"))).isFalse();
        assertThat(target.canWrite(WrappedList.class, MediaType.parseMediaType("application/xml"))).isFalse();
    }

    @Test
    public void canWriteReturnsTrueForSuitableJavaTypeAndSuitableMediaTypes() throws Exception {
        // Arrange
        CSVMessageConverter target = new CSVMessageConverter(mock(CsvMapper.class));

        // Act
        boolean result = target.canWrite(WrappedList.class, MediaType.parseMediaType("application/csv"));

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void canWriteReturnsTrueForSuitableJavaTypeAndMissingMediaTypes() throws Exception {
        // Arrange
        CSVMessageConverter target = new CSVMessageConverter(mock(CsvMapper.class));

        // Act
        boolean result = target.canWrite(WrappedList.class, null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void supportsThrowsUnsupportedOperationException() throws Exception {
        // Arrange
        CSVMessageConverter target = new CSVMessageConverter(mock(CsvMapper.class));

        // Act
        catchException(target).supports((new WrappedList<Object>()).getClass());

        // Assert
        assertThat(caughtException()).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void canReadReturnsFalse() throws Exception {
        // Arrange
        CSVMessageConverter target = new CSVMessageConverter(mock(CsvMapper.class));

        // Act
        boolean result = target.canRead((new WrappedList<Object>()).getClass(), MediaType.parseMediaType("application/csv"));

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void readInternalThrowsUnsupportedOperationException() throws Exception {
        // Arrange
        CSVMessageConverter target = new CSVMessageConverter(mock(CsvMapper.class));

        // Act
        catchException(target).readInternal(null, mock(HttpInputMessage.class));

        // Assert
        assertThat(caughtException()).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void writeInternalProducesCorrectString() throws Exception {
        // Arrange
        CSVMessageConverter target = new CSVMessageConverter(new CsvMapper());
        WrappedList<Animal> list = new WrappedList<>(Arrays.asList(new Animal("cat", 10, true), new Animal("dog", null, true), new Animal("bird", 10, false)));
        HttpOutputMessage message = mock(HttpOutputMessage.class);
        when(message.getBody()).thenReturn(new ByteArrayOutputStream());

        // Act
        target.writeInternal(list, message);

        // Assert
        assertThat(message.getBody().toString()).isEqualTo(
                "age,isMale,name\n" +
                "10,true,cat\n" +
                ",true,dog\n" +
                "10,false,bird\n");
    }

    @Test
    public void writeInternalThrowForMixedCollection() throws Exception {
        // Arrange
        CSVMessageConverter target = new CSVMessageConverter(new CsvMapper());
        WrappedList<Animal> list = new WrappedList<>(Arrays.asList(new Dog("dog", 10, true, "pug"), new Animal("cat", 10, false)));
        HttpOutputMessage message = mock(HttpOutputMessage.class);
        when(message.getBody()).thenReturn(new ByteArrayOutputStream());

        // Act
        catchException(target).writeInternal(list, message);

        // Assert
        assertThat(caughtException()).isInstanceOf(HttpMessageNotWritableException.class);
        assertThat(caughtException()).hasMessage("Can not write collection containing mixed types");
    }

    @Test
    public void writeInternalThrowForCollectionWithNull() throws Exception {
        // Arrange
        CSVMessageConverter target = new CSVMessageConverter(new CsvMapper());
        WrappedList<Animal> list = new WrappedList<>(Arrays.asList(new Animal("cat", 10, false), null));
        HttpOutputMessage message = mock(HttpOutputMessage.class);
        when(message.getBody()).thenReturn(new ByteArrayOutputStream());

        // Act
        catchException(target).writeInternal(list, message);

        // Assert
        assertThat(caughtException()).isInstanceOf(HttpMessageNotWritableException.class);
        assertThat(caughtException()).hasMessage("Can not write collection containing null");
    }

    ///CHECKSTYLE:OFF VisibilityModifier|JavadocType|FinalClass
    private class Animal {
        public Boolean isMale;
        public String name;
        public Integer age;
        private Animal(String name, Integer age, Boolean isMale) {
            this.name = name;
            this.age = age;
            this.isMale = isMale;
        }
    }

    private class Dog extends Animal {
        public String breed;
        private Dog(String name, Integer age, Boolean isMale, String breed) {
            super(name, age, isMale);
            this.breed = breed;
        }
    }
    ///CHECKSTYLE:ON
}
