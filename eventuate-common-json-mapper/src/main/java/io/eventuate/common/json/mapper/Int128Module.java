package io.eventuate.common.json.mapper;

import io.eventuate.common.id.Int128;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.deser.std.StdScalarDeserializer;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.StdScalarSerializer;

public class Int128Module extends SimpleModule {

  class IdDeserializer extends StdScalarDeserializer<Int128> {

    public IdDeserializer() {
      super(Int128.class);
    }

    public Int128 deserialize(JsonParser jp, DeserializationContext ctxt) {
      JsonToken token = jp.currentToken();
      if (token == JsonToken.VALUE_STRING) {
        String str = jp.getString().trim();
        if (str.isEmpty())
          return null;
        else
          return Int128.fromString(str);
      } else
        return (Int128) ctxt.handleUnexpectedToken(getValueType(), jp);
    }
  }

  class IdSerializer extends StdScalarSerializer<Int128> {
    public IdSerializer() {
      super(Int128.class);
    }

    public void serialize(Int128 value, JsonGenerator jgen, SerializationContext provider) {
      jgen.writeString(value.asString());
    }
  }

  @Override
  public String getModuleName() {
    return "IdJsonModule";
  }

  public Int128Module() {
    addDeserializer(Int128.class, new IdDeserializer());
    addSerializer(Int128.class, new IdSerializer());
  }

}
