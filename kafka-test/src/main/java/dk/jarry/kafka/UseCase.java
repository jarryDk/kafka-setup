package dk.jarry.kafka;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.InputMismatchException;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UseCase {

    public final static ObjectMapper objectMapper = new ObjectMapper();

    String name;
    String description;
    String topic;
    String key = UUID.randomUUID().toString();
    JsonNode record;
    KafkaProperties properties;
    Optional<JsonNode> testCase;
    Optional<User> user = Optional.empty();

    public UseCase(){}

    private UseCase(String json) throws JsonMappingException, JsonProcessingException {
        JsonNode useCase = objectMapper.readTree(json);
        this.name = useCase.findValue("name").textValue();
        this.description = useCase.findValue("description").textValue();
        this.topic = useCase.findValue("topic").textValue();
        this.key = useCase.findValue("key").textValue();
        this.record = useCase.findValue("record");
        this.properties = new KafkaProperties(Optional.ofNullable(useCase.findValue("properties")));
        this.testCase = Optional.ofNullable(useCase.findValue("testCase"));
    }

    public static UseCase of(String json) throws JsonMappingException, JsonProcessingException {
        if (json.contains("${uuid}")) {
            json = json.replace("${uuid}", UUID.randomUUID().toString());
        }
        return new UseCase(json);
    }

    public static UseCase of(Path path) throws IOException {
        String json = Files.readString(path);
        return of(json);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public KafkaProperties getKafkaProperties() {
        return properties;
    }

    public Optional<JsonNode> getTestCase() {
        return testCase;
    }

    public String getKey() {
        return key;
    }

    public JsonNode getRecord() {
        return record;
    }

    public String getTopic() {
        return topic;
    }

    public UseCase setUserName(String username) {
        properties.put("username", username);
        return this;
    }

    public UseCase setPassword(String password) {
        properties.put("password", password);
        return this;
    }

    public UseCase setUser(User user) {
        properties.put("username", user.name());
        properties.put("password", user.password());
        this.user = Optional.of(user);
        return this;
    }

    /**
     * username need to have an value or this methode will throw an
     * InputMismatchException
     * 
     * @param username
     * @param users
     * @return UseCase
     */
    public UseCase setPassword(String username, UserCollection users) {
        if (username == null || username.isEmpty()) {
            throw new InputMismatchException("Missing username in input");
        }
        User user = users.getUser(username);
        if (user == null) {
            throw new InputMismatchException("Missing username/password in KAFKA_USER_PASSWORD_JSON");
        }
        setUser(user);
        return this;
    }

    /**
     * username need to be set in UseCase or this methode will throw an
     * InputMismatchException
     * 
     * @param users
     * @return UseCase
     */
    public UseCase setPassword(UserCollection users) {
        String username = (String) properties.get("username");
        setPassword(username, users);
        return this;
    }

    public Optional<User> getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "UseCase [name=" + name +
                ", description=" + description +
                ", properties=" + properties +
                ", key=" + key +
                ", record=" + record +
                ", topic=" + topic + "]";
    }

}
