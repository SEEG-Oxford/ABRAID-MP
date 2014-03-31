package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.views.support;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;

/**
 * Adapted from https://github.com/martypitt/JsonViewExample.
 *
 * Adds support for Jackson's JsonView on methods annotated with a {@link ResponseView} annotation.
 * @author martypitt
 */
public class ViewAwareJsonMessageConverter extends
        MappingJackson2HttpMessageConverter {

    @Override
    protected void writeInternal(Object object, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        if (object instanceof DataView) {
            writeView((DataView) object, outputMessage);
        } else {
            super.writeInternal(object, outputMessage);
        }
    }

    private void writeView(DataView view, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        JsonEncoding encoding = getJsonEncoding(outputMessage.getHeaders().getContentType());
        ObjectWriter writer = getObjectMapper().writerWithView(view.getView());
        JsonGenerator jsonGenerator = writer.getJsonFactory().createJsonGenerator(outputMessage.getBody(), encoding);
        try {
            writer.writeValue(jsonGenerator, view.getData());
        } catch (IOException ex) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
        }
    }
}
