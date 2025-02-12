package com.planktonsoft;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;

public class TryObjectMapper extends ObjectMapper {
    public <T> Try<T> tryReadValue(String msg, Class<T> valueType) {
        return Try.of(() -> this.readValue(msg, valueType));
    }
}
