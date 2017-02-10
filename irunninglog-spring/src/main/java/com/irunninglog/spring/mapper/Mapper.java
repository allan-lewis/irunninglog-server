package com.irunninglog.spring.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.irunninglog.api.mapping.IMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
final class Mapper implements IMapper {

    private static final Logger LOG = LoggerFactory.getLogger(Mapper.class);

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String encode(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            LOG.error("encode:illegal:" + object, ex);
            throw new IllegalArgumentException("Can't encode " + object, ex);
        }
    }

    @Override
    public <T> T decode(String string, Class<T> clazz) {
        try {
            return mapper.readValue(string, clazz);
        } catch (IOException ex) {
            LOG.error("decode:illegal:" + string + ":" + clazz, ex);
            throw new IllegalArgumentException("Can't decode " + string + " " + clazz, ex);
        }
    }

}