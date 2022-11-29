package dk.jarry.kafka;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class UseCaseTest {

    private static final Logger LOGGER = Logger.getLogger(UseCaseTest.class.getName());

    @Test
    public void construstor() throws JsonMappingException, JsonProcessingException{
        String json = """
            {
                "name":"-name",
                "description":"-description",
                "topic":"-topic",
                "key":"-key",
                "record":{
                    "subject":"-Subject",
                    "body":"-body",
                    "uuid": "c995d34b-7a27-45e4-8923-b61f21bf2421"
                },
                "properties": {
                    "sasl.mechanism":"PLAIN",
                    "username":"UserName",
                    "password":"Password"
                }
            }
            """;
        UseCase useCase = UseCase.of(json);

        String expected ="""
            UseCase [name=-name, description=-description, properties=KafkaProperties [map={sasl.mechanism=PLAIN, username=UserName}], key=-key, record={"subject":"-Subject","body":"-body","uuid":"c995d34b-7a27-45e4-8923-b61f21bf2421"}, topic=-topic]""";
        String actual = useCase.toString();
        LOGGER.log(Level.INFO, useCase.toString());
        assertEquals(expected, actual);

    }

}
