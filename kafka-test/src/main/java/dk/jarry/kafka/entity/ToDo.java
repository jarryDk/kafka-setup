package dk.jarry.kafka.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class ToDo {

    @JsonProperty
    public String subject;

    @JsonProperty
    public String body;

    @JsonProperty
    public String uuid;

    public ToDo() {
    }

    public ToDo(String subject, String body, String uuid) {
        this.subject = subject;
        this.body = body;
        this.uuid = uuid;
    }

    public ToDo(JsonNode record) {
        this.subject = getString(record, "subject");
        this.body = getString(record, "body");
        this.uuid = getString(record, "uuid");
    }

    String getString(JsonNode record, String key) {
        return record.has(key) ? record.findValue(key).asText() : null;
    }

    @Override
    public String toString() {
        return "ToDo [subject=" + subject + ", body=" + body + ", uuid=" + uuid + "]";
    }

}
