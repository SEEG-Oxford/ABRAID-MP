package uk.ac.ox.zoo.seeg.abraid.mp.common.dto;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.WrappedList;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * A spring message converter for producing CSV text from DTO objects.
 * Copyright (c) 2014 University of Oxford
 */
public class CSVMessageConverter
        extends AbstractHttpMessageConverter<WrappedList<?>> {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final String CANNOT_WRITE_NULL = "Cannot write collection containing null";
    private static final String CANNOT_WRITE_MIXED_TYPES = "Cannot write collection containing mixed types";
    private final CsvMapper csvMapper;

    public CSVMessageConverter(CsvMapper csvMapper) {
        super(
                new MediaType("application", "csv", DEFAULT_CHARSET),
                new MediaType("application", "*+csv", DEFAULT_CHARSET));
        this.csvMapper = csvMapper;
    }

    /**
     * Checks if the given class is supported for reading, and if the given media type is supported.
     * @param clazz the class to test for support
     * @param mediaType the media type to test for support
     * @return {@code true} if supported; {@code false} otherwise
     */
    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        // Don't support any csv parsing
        return false;
    }

    /**
     * Checks if the given class is supported for writing, and if the given media type is supported.
     * @param clazz the class to test for support
     * @param mediaType the media type to test for support
     * @return {@code true} if supported; {@code false} otherwise
     */
    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return WrappedList.class.isAssignableFrom(clazz) && canWrite(mediaType);
    }

    /**
     * Indicates whether the given class is supported by this converter.
     * @param clazz the class to test for support
     * @return {@code true} if supported; {@code false} otherwise
     */
    @Override
    protected boolean supports(Class<?> clazz) {
        // Should not be called, since we override canRead/canWrite instead
        throw new UnsupportedOperationException();
    }

    /**
     * Implements the abstract template method that reads the actual object. Invoked from {@link #read}.
     * @param clazz the type of object to return
     * @param inputMessage the HTTP input message to read from
     * @return the converted object
     * @throws IOException in case of I/O errors
     * @throws HttpMessageNotReadableException in case of conversion errors
     */
    @Override
    protected WrappedList<?> readInternal(Class<? extends WrappedList<?>> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        // Should not be called, since we return false for all canRead
        throw new UnsupportedOperationException();
    }

    /**
     * Implements the abstract template method that writes the actual body. Invoked from {@link #write}.
     * @param t the object to write to the output message
     * @param outputMessage the HTTP output message to write to
     * @throws IOException in case of I/O errors
     * @throws HttpMessageNotWritableException in case of conversion errors
     */
    @Override
    protected void writeInternal(WrappedList<?> t, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        Iterator<?> it = t.getList().iterator();
        Class<?> listType = null;
        while (it.hasNext()) {
            Object entry = it.next();
            if (entry == null) {
                throw new HttpMessageNotWritableException(CANNOT_WRITE_NULL);
            }

            Class<?> entryType = entry.getClass();
            if (listType == null) {
                listType = entryType;
            } else if (!entryType.equals(listType)) {
                throw new HttpMessageNotWritableException(CANNOT_WRITE_MIXED_TYPES);
            }
        }

        if (listType != null) {
            CsvSchema schema = csvMapper.schemaFor(listType).withHeader();
            csvMapper.writer(schema).writeValue(outputMessage.getBody(), t.getList());
        }
    }
}
