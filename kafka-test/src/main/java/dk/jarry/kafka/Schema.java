package dk.jarry.kafka;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import javax.enterprise.context.Dependent;

import io.apicurio.registry.rest.client.RegistryClient;
import io.apicurio.registry.rest.client.RegistryClientFactory;
import io.apicurio.registry.rest.v2.beans.IfExists;
import io.apicurio.registry.types.ArtifactType;

@Dependent
public class Schema {

    public static final String TODO_SCHEMA = """
            {
                "title": "ToDo",
                "description": "A ToDo in the catalog",
                "type": "object",
                "subject": {
                    "description": "Subject of todo",
                    "type": "string"
                },
                "body": {
                    "description": "Body of todo",
                    "type": "string"
                },
                "uuid": {
                    "description": "Uuid of todo",
                    "type": "string"
                },
                "required": [ "subject" ]
            }""";

    public static final String TICKET_SCHEMA = """
            {
                "title": "Ticket",
                "description": "A Ticket in the catalog",
                "type": "object",
                "subject": {
                    "description": "Subject of ticket",
                    "type": "string"
                },
                "registrationNumber": {
                    "description": "Registration number of ticket",
                    "type": "string"
                },
                "body": {
                    "description": "Body of ticket",
                    "type": "string"
                },
                "uuid": {
                    "description": "Uuid of todo",
                    "type": "string"
                },
                "required": [ "subject" , "registrationNumber" ]
            }""";

    public Schema() {
    }

    public void createSchemas(String registryUrl, String groupId) {
        RegistryClient client = RegistryClientFactory.create(registryUrl);

        client.createArtifact(groupId, "ToDo", ArtifactType.JSON, IfExists.RETURN_OR_UPDATE,
                new ByteArrayInputStream(Schema.TODO_SCHEMA.getBytes(StandardCharsets.UTF_8)));

        client.createArtifact(groupId, "Ticket", ArtifactType.JSON, IfExists.RETURN_OR_UPDATE,
                new ByteArrayInputStream(Schema.TICKET_SCHEMA.getBytes(StandardCharsets.UTF_8)));
    }

}
