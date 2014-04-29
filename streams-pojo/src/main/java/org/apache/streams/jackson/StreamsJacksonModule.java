package org.apache.streams.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.Map;

/**
 * Created by sblackmon on 3/27/14.
 */
public class StreamsJacksonModule extends SimpleModule {

    public StreamsJacksonModule() {
        super();
        addSerializer(DateTime.class, new StreamsDateTimeSerializer(DateTime.class));
        addDeserializer(DateTime.class, new StreamsDateTimeDeserializer(DateTime.class));

        addSerializer(Period.class, new StreamsPeriodSerializer(Period.class));
        addDeserializer(Period.class, new StreamsPeriodDeserializer(Period.class));

        addSerializer(Map.class, new StreamsMapSerializer(Map.class));

    }


}
