package dk.jarry.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import dk.jarry.kafka.entity.Ticket;
import dk.jarry.kafka.entity.ToDo;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class CreateTopics {

    private static final Logger LOGGER = Logger.getLogger(CreateTopics.class.getName());

    @Inject
    KafkaUseCaseSetup setup;

    @Inject
    UserCollection users;

    // @Test
    public void createTopicUseCase201() throws JsonMappingException, JsonProcessingException {
        String json = """
                {
                    "name":"createTopicUseCase201",
                    "description":"Create topic ToDo",
                    "topic":"ToDo",
                    "key":"-key",
                    "record":{
                        "subject":"Subject - createTopicUseCase201",
                        "body":"Incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam",
                        "uuid": "${uuid}"
                    }
                }""";
        json = String.format(json, UUID.randomUUID().toString());
        String userName = "user1";
        User user = users.getUser(userName);
        UseCase useCase = UseCase.of(json).setUser(user);
        setup.createTopicUseCase(useCase);
    }

    // @Test
    public void createTopicUseCase202() throws JsonMappingException, JsonProcessingException {
        String json = """
                {
                    "name":"createTopicUseCase202",
                    "description":"Create topic Foo",
                    "topic":"Foo",
                    "topic.numPartitions":"2",
                    "topic.replicationFactor":"2",
                    "key":"-key",
                    "record":{
                        "subject":"Subject - createTopicUseCase202",
                        "body":"Incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam",
                        "uuid": "${uuid}"
                    },
                    "properties": {
                        "apicurio.registry.auto-register":"true"
                    }
                }""";
        json = String.format(json, UUID.randomUUID().toString());
        String userName = "user1";
        User user = users.getUser(userName);
        UseCase useCase = UseCase.of(json).setUser(user);
        setup.createTopicUseCase(useCase);
    }

}
