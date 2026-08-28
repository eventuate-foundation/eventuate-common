package io.eventuate.common.json.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.core.JacksonException;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

public class JSonMapper {

  public static ObjectMapper objectMapper = JsonMapper.builder()
          .enable(StreamWriteFeature.WRITE_BIGDECIMAL_AS_PLAIN)
          .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
          .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
          .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_ABSENT))
          .addModule(new Int128Module())
          .build();

  public static String toJson(Object x) {
    try {
      return objectMapper.writeValueAsString(x);
    } catch (JacksonException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> T fromJson(String json, Class<T> targetType) {
    try {
      return objectMapper.readValue(json, targetType);
    } catch (JacksonException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> T fromJsonByName(String json, String targetType) {
    try {
      return objectMapper.readValue(json, (Class<T>) Thread.currentThread().getContextClassLoader().loadClass(targetType));
    } catch (JacksonException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
