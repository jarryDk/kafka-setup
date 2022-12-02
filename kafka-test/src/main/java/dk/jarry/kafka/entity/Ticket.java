package dk.jarry.kafka.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class Ticket {

    @JsonProperty
    public String subject;

    @JsonProperty
    public String body;

    @JsonProperty
    public String uuid;

    @JsonProperty(required=true)
    public String registrationNumber;

    public Ticket() {}

    public Ticket(String subject, String body, String uuid, String registrationNumber) {
        this.subject = subject;
        this.body = body;
        this.uuid = uuid;
        this.registrationNumber = registrationNumber;
    }

    public Ticket(JsonNode record) {
        this.subject = getString(record, "subject");
        this.body = getString(record, "body");
        this.uuid = getString(record, "uuid");
        this.registrationNumber = getString(record, "registrationNumber");
    }

    String getString(JsonNode record, String key) {
        return record.has(key) ? record.findValue(key).asText() : null;
    }

    @Override
    public String toString() {
        return "Ticket [subject=" + subject + ", body=" + body + ", uuid=" + uuid + ", registrationNumber="
                + registrationNumber + "]";
    }  

}
