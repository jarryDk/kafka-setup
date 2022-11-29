package dk.jarry.kafka;

import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

public class UseCase {

    String name;
    String description;
    String topic;
    String key = UUID.randomUUID().toString();
    JsonNode record;
    KafkaProperties properties;
    Optional<JsonNode> testCase;
    Optional<User> user = Optional.empty();

    private UseCase(String json) throws JsonMappingException, JsonProcessingException {
        JsonNode useCase = KafkaUseCaseSetup.objectMapper.readTree(json);
        this.name = useCase.findValue("name").textValue();
        this.description = useCase.findValue("description").textValue();
        this.topic = useCase.findValue("topic").textValue();
        this.key = useCase.findValue("key").textValue();
        this.record = useCase.findValue("record");
        this.properties = new KafkaProperties(Optional.ofNullable(useCase.findValue("properties")));
        this.testCase = Optional.ofNullable(useCase.findValue("testCase"));
    }

    public static UseCase of(String json) throws JsonMappingException, JsonProcessingException {
        return new UseCase(json);
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

    public UseCase setUserName(String username){
        properties.getMap().put("username", username);
        return this;
    }

    public UseCase setPassword(String password){
        properties.getMap().put("password", password);
        return this;
    }

    public UseCase setUser(User user){
        properties.getMap().put("username", user.name());
        properties.getMap().put("password", user.password());
        this.user = Optional.of(user);
        return this;
    }

    public Optional<User> getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "UseCase [name=" + name +
                ", description=" + description +
                ", properties=" + properties.toString() +
                ", key=" + key +
                ", record=" + record.toString() +
                ", topic=" + topic + "]";
    }

}
