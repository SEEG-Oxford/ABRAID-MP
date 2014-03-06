package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * Created by zool1112 on 06/03/14.
 */
public class JsonObjectMapper extends ObjectMapper {
    public JsonObjectMapper(){
        super();
        this.registerModule(new JodaModule());
        this.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
}
