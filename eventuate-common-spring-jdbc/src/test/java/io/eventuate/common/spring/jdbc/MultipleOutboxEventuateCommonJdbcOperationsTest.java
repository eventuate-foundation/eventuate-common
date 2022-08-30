package io.eventuate.common.spring.jdbc;

import io.eventuate.common.id.IdGenerator;
import io.eventuate.common.jdbc.EventuateCommonJdbcOperations;
import io.eventuate.common.jdbc.EventuateSchema;
import io.eventuate.common.jdbc.OutboxPartitioningSpec;
import io.eventuate.common.testcontainers.EventuateMySqlContainer;
import io.eventuate.common.testcontainers.PropertyProvidingContainer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootTest(classes = MultipleOutboxEventuateCommonJdbcOperationsTest.Config.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleOutboxEventuateCommonJdbcOperationsTest {

    public static final int OUTBOX_TABLES = 8;
    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private EventuateCommonJdbcOperations eventuateCommonJdbcOperations;
    @Autowired
    private EventuateSchema eventuateSchema;

    @Configuration
    @Import(EventuateCommonJdbcOperationsTest.Config.class)
    static class Config {

        @Bean
        public OutboxPartitioningSpec outboxPartitioningSpec() {
            return new OutboxPartitioningSpec(OUTBOX_TABLES, null);
        }
    }

    //@ClassRule
    public static EventuateMySqlContainer mysql =
            new EventuateMySqlContainer(FileSystems.getDefault().getPath(("../mysql/Dockerfile-mysql8")))
                    .withEnv("EVENTUATE_OUTBOX_TABLES", Integer.toString(OUTBOX_TABLES))
                    .withReuse(true);

    @DynamicPropertySource
    static void registerMySqlProperties(DynamicPropertyRegistry registry) {
        PropertyProvidingContainer.startAndProvideProperties(registry, mysql);
    }



    @Test
    public void shouldInsertIntoMultipleOutboxes() {
        String payload = "{}";
        String destination = "myChannel";
        Map<String, String> headers = new HashMap<>();
        headers.put("ID", UUID.randomUUID().toString());
        headers.put("PARTITION_ID", UUID.randomUUID().toString());
        eventuateCommonJdbcOperations.insertIntoMessageTable(idGenerator, payload, destination, headers, eventuateSchema);
    }
}
