package io.eventuate.common.quarkus.id;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Collections;
import java.util.Map;

public class DatabaseIdProfile implements QuarkusTestProfile {
  @Override
  public Map<String, String> getConfigOverrides() {
    return Collections.singletonMap("eventuate.outbox.id", "1");
  }
}
