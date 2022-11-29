package dk.jarry.kafka;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class KafkaUseCaseSetupTest {

    private static final Logger LOGGER = Logger.getLogger(KafkaUseCaseSetupTest.class.getName());

    @Inject
    KafkaUseCaseSetup setup;

    @Inject
    UserCollection users;

    @Test
    public void users() {
        String userName = "user1";
        User user = users.getUser(userName);
        LOGGER.log(Level.INFO, user.toString());
    }

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
                        "uuid": "%s"
                    },
                    "properties": {
                        "sasl.mechanism":"PLAIN"
                    }
                }""";
        json = String.format(json, UUID.randomUUID().toString());
        String userName = "user1";
        User user = users.getUser(userName);
        UseCase useCase = UseCase.of(json).setUser(user);
        setup.addToTopicUseCase(useCase);
    }

    @Test
    public void addToTopicUseCase102() throws JsonMappingException, JsonProcessingException {
        String json = """
                {
                    "name":"addToTopicUseCase102",
                    "description":"Add to topic Ticket with missing registrationNumber - Bumpy flow",
                    "topic":"Ticket",
                    "key":"PartitionKey",
                    "record":{
                        "subject":"Subject - addToTopicUseCase102",
                        "body":"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor",
                        "uuid": "%s",
                        "foo":"boo"
                    },
                    "properties": {
                        "sasl.mechanism":"PLAIN"
                    },
                    "testCase":{
                        "org.everit.json.schema.ValidationException": "true"
                    }
                }""";
        json = String.format(json, UUID.randomUUID().toString());
        String userName = "user1";
        User user = users.getUser(userName);
        UseCase useCase = UseCase.of(json).setUser(user);
        setup.addToTopicUseCase(useCase);
    }

    @Test
    public void addToTopicUseCase103() throws JsonMappingException, JsonProcessingException {
        String json = """
                {
                    "name":"addToTopicUseCase103",
                    "description":"Add to topic Ticket (one too many field) - Happy flow",
                    "topic":"Ticket",
                    "key":"PartitionKey",
                    "record":{
                        "subject":"Subject - addToTopicUseCase103",
                        "body":"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor",
                        "uuid": "%s",
                        "registrationNumber": "42",
                        "foo":"boo"
                    },
                    "properties": {
                        "sasl.mechanism":"PLAIN"
                    }
                }""";
        json = String.format(json, UUID.randomUUID().toString());
        String userName = "user1";
        User user = users.getUser(userName);
        UseCase useCase = UseCase.of(json).setUser(user);
        setup.addToTopicUseCase(useCase);
    }

    @Test
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
                        "uuid": "%s"
                    }
                }""";
        json = String.format(json, UUID.randomUUID().toString());
        String userName = "user1";
        User user = users.getUser(userName);
        UseCase useCase = UseCase.of(json).setUser(user);
        setup.createTopicUseCase(useCase);
    }

}
