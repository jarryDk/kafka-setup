package dk.jarry.kafka;

import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;

public class KafkaProperties extends Properties {

    public KafkaProperties(Optional<JsonNode> properties) {
        properties.ifPresent(prop -> prop.fieldNames().forEachRemaining(
                key -> super.put(key, prop.get(key).asText())));
    }

    public void setUserName(String username) {
        super.put("username", username);
    }

    public void setPassword(String password) {
        super.put("password", password);
    }

    public String toString() {
        return "KafkaProperties [map=" +
                super.entrySet().stream()
                        .filter(set -> !set.getKey().equals("password"))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                + "]";
    }

}
