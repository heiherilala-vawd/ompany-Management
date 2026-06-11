package com.example.demo.config;

import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.boot.jackson.JacksonComponent;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

@JacksonComponent
public class JacksonConfig {

  @SuppressWarnings("rawtypes")
  public static class JsonNullableSerializer extends StdSerializer<JsonNullable> {
    public JsonNullableSerializer() {
      super(JsonNullable.class, false);
    }

    @Override
    public void serialize(JsonNullable value, JsonGenerator gen, SerializationContext ctxt)
        throws JacksonException {
      if (value == null || !value.isPresent()) {
        ctxt.defaultSerializeNullValue(gen);
        return;
      }
      ctxt.writeValue(gen, value.get());
    }
  }
}
