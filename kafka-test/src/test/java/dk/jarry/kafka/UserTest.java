package dk.jarry.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class UserTest {

    private static final Logger LOGGER = Logger.getLogger(ProduceToTopic.class.getName());

    @Inject
    KafkaUseCaseSetup setup;

    @Inject
    UserCollection users;

    // @Test
    public void findUserWithSucces() {
        String userName = "user1";
        User user = users.getUser(userName);
        LOGGER.log(Level.INFO, user.toString());
    }

    // @Test
    public void findUseCaseWithFailure() throws JsonMappingException, JsonProcessingException {
        String json = """
                {
                    "name":"addToTopicUseCase105",
                    "description":"Add to topic ToDo with wrong username - Failed flow",
                    "topic":"ToDo",
                    "key":"PartitionKey",
                    "record":{
                        "subject":"Subject - addToTopicUseCase105",
                        "body":"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor",
                        "uuid": "${uuid}"
                    },
                    "properties": {
                        "username":"wrong_username",
                        "sasl.mechanism":"PLAIN"
                    },
                    "testCase":{
                        "java.util.InputMismatchException": "Missing username/password in KAFKA_USER_PASSWORD_JSON"
                    }
                }""";
        UseCase useCase = UseCase.of(json);
        String expected = "Missing username/password in KAFKA_USER_PASSWORD_JSON";
        String actual = null;
        try {
            useCase.setPassword(users);
        } catch (Exception e) {
            Map<String, String> cases = new HashMap<>();
            useCase.getTestCase().ifPresent(
                    prop -> prop.fieldNames().forEachRemaining(
                            key -> cases.put(key, prop.get(key).asText())));
            String key = e.getClass().getName();
            if (cases.containsKey(key) &&
                    cases.get(key).equals(e.getMessage())) {
                LOGGER.log(Level.INFO, " - " + e.getMessage());
                actual = cases.get(key);
            } else {
                LOGGER.log(Level.WARNING, " - " + e.getMessage());
            }
        }
        assertEquals(expected, actual);
    }

}
