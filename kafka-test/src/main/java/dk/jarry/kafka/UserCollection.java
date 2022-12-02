package dk.jarry.kafka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

@Dependent
public class UserCollection {

    public final static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * <code>
     * export KAFKA_USER_PASSWORD_JSON='[{"name":"user1","password":"password1"}]'
     * </code>
     */
    @ConfigProperty(name = "KAFKA_USER_PASSWORD_JSON", defaultValue = "[{\"name\":\"user1\",\"password\":\"password1\"}]")
    String kafkaUserPasswordJson;

    Map<String, User> users = new HashMap<>();

    public UserCollection() {
    }

    @PostConstruct
    public void init() {
        try {
            setUsers(kafkaUserPasswordJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public User getUser(String userName) {
        return users.get(userName);
    }

    private void setUsers(String kafkaUserPasswordJson) throws JsonMappingException, JsonProcessingException {
        // Yes - It is a hack
        if (kafkaUserPasswordJson.startsWith("'") &&
                kafkaUserPasswordJson.endsWith("'")) {
            kafkaUserPasswordJson = kafkaUserPasswordJson.substring(1, kafkaUserPasswordJson.length() - 1);
        }
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        List<User> userList = objectMapper.readValue( //
                kafkaUserPasswordJson, //
                typeFactory.constructCollectionType( //
                        List.class, //
                        User.class));
        userList.forEach(user -> users.put(user.name(), user));
    }

}
