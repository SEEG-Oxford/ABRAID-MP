package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.views.support;

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
import java.util.Iterator;

public class CSVMessageConverter
        extends AbstractHttpMessageConverter<WrappedList<?>> {
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private CsvMapper csvMapper = new CsvMapper();

    public CSVMessageConverter() {
        super(
                new MediaType("application", "csv", DEFAULT_CHARSET),
                new MediaType("application", "*+csv", DEFAULT_CHARSET));
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return WrappedList.class.isAssignableFrom(clazz) && this.csvMapper.canSerialize(clazz) && canWrite(mediaType);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        // should not be called, since we override canRead/Write instead
        throw new UnsupportedOperationException();
    }

    @Override
    protected WrappedList<?> readInternal(Class<? extends WrappedList<?>> aClass, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
        // should not be called, since we return false for all canReads
        throw new UnsupportedOperationException();
    }

    @Override
    protected void writeInternal(WrappedList<?> collection, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
        Iterator it = collection.getList().iterator();
        if (it.hasNext()){
            CsvSchema schema = csvMapper.schemaFor(it.next().getClass()).withHeader();
            csvMapper.writer(schema).writeValue(httpOutputMessage.getBody(), collection.getList());
        }
    }
}
