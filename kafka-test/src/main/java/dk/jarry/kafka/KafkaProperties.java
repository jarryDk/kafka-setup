package dk.jarry.kafka;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;

public class KafkaProperties {

    private Map<String,String> map = new HashMap<>();

    public KafkaProperties(Optional<JsonNode> properties){
        properties.ifPresent( prop ->
            prop.fieldNames().forEachRemaining(
                key -> map.put(key, prop.get(key).asText())
            )
        );
    }

    public Map<String,String> getMap(){
        return map;
    }

    public void setUserName(String username){
        map.put("username", username);
    }

    public void setPassword(String password){
        map.put("password", password);
    }

    @Override
    public String toString() {
        Map<String,String> mapToString = map.entrySet().stream()
            .filter( keySet -> !keySet.getKey().equals("password"))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return "KafkaProperties [map=" + mapToString + "]";
    }

}
