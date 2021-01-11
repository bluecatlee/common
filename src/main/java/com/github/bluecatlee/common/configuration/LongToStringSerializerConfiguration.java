package com.github.bluecatlee.common.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class LongToStringSerializerConfiguration {

    @Bean
    public ObjectMapper ObjectMapper() {
        ToStringSerializer seriesInstance = new ToStringSerializer() {
            @Override
            public void serialize(Object value, JsonGenerator gen, SerializerProvider provider)
                    throws IOException {
                Long value1 = (Long) value;
                if (value1 > 9007199254740991L) {
                    gen.writeString(value.toString());
                } else {
                    gen.writeNumber(value1);
                }
            }
        };
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, seriesInstance);
        simpleModule.addSerializer(Long.TYPE, seriesInstance);
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule())
                .registerModule(simpleModule);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

}
