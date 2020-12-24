package io.eventuate.common.quarkus.id;

import io.eventuate.common.id.IdGenerator;
import io.eventuate.common.id.DatabaseIdGenerator;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTest
@TestProfile(DatabaseIdProfile.class)
public class DatabaseIdGeneratorConfigurationTest {

  @Inject
  IdGenerator idGenerator;

  @Test
  public void testThatDatabaseIdGeneratorIsUsed() {
    Assertions.assertEquals(DatabaseIdGenerator.class, idGenerator.getClass());
  }
}