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
public class ProduceToTopic {

    private static final Logger LOGGER = Logger.getLogger(ProduceToTopic.class.getName());

    @Inject
    KafkaUseCaseSetup setup;

    @Inject
    UserCollection users;

    @Test
    public void addToTopicUseCase101() throws JsonMappingException, JsonProcessingException {
        String json = """
                {
                    "name":"addToTopicUseCase101",
                    "description":"Add to topic ToDo - Happy flow",
                    "topic":"ToDo",
                    "key":"PartitionKey",
                    "record":{
                        "subject":"Subject - addToTopicUseCase101",
                        "body":"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor",
                        "uuid": "${uuid}"
                    },
                    "properties": {
                        "username":"user1",
                        "sasl.mechanism":"PLAIN"
                    }
                }""";
        UseCase useCase = UseCase.of(json).setPassword(users);
        ToDo toDo = new ToDo(useCase.record);
        setup.addToTopic(useCase, toDo);
    }

    @Test
    public void addToTopicUseCase102() throws IOException {
        Path path = Paths.get("usecases/addToTopicUseCase102.json");
        UseCase useCase = UseCase.of(path).setPassword(users);
        ToDo toDo = new ToDo(useCase.record);
        setup.addToTopic(useCase, toDo);
    }

    @Test
    public void addToTopicUseCase103() throws JsonMappingException, JsonProcessingException {
        String json = """
                {
                    "name":"addToTopicUseCase103",
                    "description":"Add to topic Ticket with missing registrationNumber - Bumpy flow",
                    "topic":"Ticket",
                    "key":"PartitionKey",
                    "record":{
                        "subject":"Subject - addToTopicUseCase103",
                        "body":"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor",
                        "uuid": "${uuid}"
                    },
                    "properties": {
                        "sasl.mechanism":"PLAIN"
                    },
                    "testCase":{
                        "org.everit.json.schema.ValidationException": "#: required key [registrationNumber] not found"
                    }
                }""";
        String userName = "user1";
        User user = users.getUser(userName);
        UseCase useCase = UseCase.of(json).setUser(user);
        Ticket ticket = new Ticket(useCase.record);
        setup.addToTopic(useCase, ticket);
    }
}
