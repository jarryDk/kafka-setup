package dk.jarry.kafka;

import java.util.Collections;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.serialization.StringSerializer;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.apicurio.registry.serde.SerdeConfig;

@Dependent
public class KafkaUseCaseSetup {

    private static final Logger LOGGER = Logger.getLogger(KafkaUseCaseSetup.class.getName());

    public final static ObjectMapper objectMapper = new ObjectMapper();

    @ConfigProperty(name = "GROUP_ID", defaultValue = "unit-test")
    String groupId;

    @ConfigProperty(name = "REGISTRY_URL", defaultValue = "http://localhost:8081/apis/registry/v2")
    String registryUrl;

    @ConfigProperty(name = "REGISTRY_TYPE", defaultValue = "Apicurio")
    String registryType;

    @ConfigProperty(name = "KAFKA_BOOTSTRAP_SERVERS", defaultValue = "localhost:9092")
    String kafkaBootstrapServers;

    @Inject
    Schema schema;

    public KafkaUseCaseSetup() {
    }

    @PostConstruct
    public void init() {
        try {
            schema.createSchemas(registryUrl, groupId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addToTopicUseCase(UseCase useCase) {
        startUseCase(useCase);
        try {
            addRecordToTopic(useCase);
        } catch (org.everit.json.schema.ValidationException e) {
            useCase.getTestCase().ifPresentOrElse(
                    testCase -> {
                        JsonNode node = testCase.findValue("org.everit.json.schema.ValidationException");
                        if (node != null && "true".equals(node.asText())) {
                            LOGGER.log(Level.INFO, " - " + e.getMessage());
                        } else {
                            LOGGER.log(Level.WARNING, " - " + e.getMessage());
                        }
                    },
                    () -> LOGGER.log(Level.WARNING, " - " + e.getMessage()));
        }
        endUseCase(useCase);
    }

    public void createTopicUseCase(UseCase useCase) {
        startUseCase(useCase);
        createTopic(useCase);
        endUseCase(useCase);
    }

    private void startUseCase(UseCase useCase) {
        LOGGER.log(Level.INFO, "Start use case " + useCase.getName());
        LOGGER.log(Level.INFO, " - Description: " + useCase.getDescription());
        useCase.getUser().ifPresent(user -> LOGGER.log(Level.INFO, " - User: " + user.name()));
    }

    private void endUseCase(UseCase useCase) {
        LOGGER.log(Level.INFO, "End use case " + useCase.getName());
    }

    private void addRecordToTopic(UseCase useCase) {
        String key = useCase.getKey();
        JsonNode record = useCase.getRecord();
        String topic = useCase.getTopic();
        LOGGER.log(Level.INFO, String.format(" - Producing record (%s): %s", key, record));
        try (Producer<String, JsonNode> producer = new KafkaProducer<String, JsonNode>(getProperties(useCase))) {
            producer.send(new ProducerRecord<String, JsonNode>(topic, key, record), new Callback() {
                @Override
                public void onCompletion(RecordMetadata m, Exception e) {
                    if (e != null) {
                        LOGGER.log(Level.SEVERE, String.format(" - No record was produced - error: %s",
                                e.getMessage()));
                    } else {
                        LOGGER.log(Level.INFO, String.format(" - Produced record --> t:%s\tp:%s\to:%s",
                                m.topic(),
                                m.partition(),
                                m.offset()));
                    }
                }
            });
        }
    }

    private void createTopic(UseCase useCase) {
        String topic = useCase.getTopic();
        Properties props = getProperties(useCase);
        final NewTopic newTopic = new NewTopic(topic, Optional.empty(), Optional.empty());
        try (final AdminClient adminClient = AdminClient.create(props)) {
            adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
        } catch (final InterruptedException | ExecutionException e) {
            if (!(e.getCause() instanceof TopicExistsException)) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Get Properties for UseCase
     */
    private Properties getProperties(UseCase useCase) {
        Properties props = new Properties();
        props.putAll(useCase.getKafkaProperties().getMap());
        // Configure kafka settings
        props.putIfAbsent(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        props.putIfAbsent(ProducerConfig.CLIENT_ID_CONFIG, "Producer-UnitTest");
        props.putIfAbsent(ProducerConfig.ACKS_CONFIG, "all");
        if ("Apicurio".equals(registryType)) {
            props.putIfAbsent(SerdeConfig.REGISTRY_URL, registryUrl);
            props.putIfAbsent(SerdeConfig.AUTO_REGISTER_ARTIFACT, Boolean.FALSE);
            props.putIfAbsent(SerdeConfig.EXPLICIT_ARTIFACT_GROUP_ID, groupId);
            props.putIfAbsent(SerdeConfig.EXPLICIT_ARTIFACT_ID, useCase.getTopic());
            props.putIfAbsent(SerdeConfig.VALIDATION_ENABLED, Boolean.TRUE);
            props.putIfAbsent(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            props.putIfAbsent(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                    io.apicurio.registry.serde.jsonschema.JsonSchemaKafkaSerializer.class.getName());
        } else {
            props.putIfAbsent("schema.registry.url", registryUrl);
            props.putIfAbsent("auto.register.schemas", Boolean.FALSE);
            props.putIfAbsent(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            props.putIfAbsent(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                    io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer.class.getName());
        }
        return props;
    }

}
