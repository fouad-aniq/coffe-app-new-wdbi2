package ai.shreds.infrastructure.config;

import ai.shreds.infrastructure.external_services.InfrastructureCategoryEventPublisher;
import ai.shreds.shared.SharedCategoryEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
public class InfrastructureKafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, SharedCategoryEvent> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        configProps.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "category-events-transaction");
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, SharedCategoryEvent> configureProducer() {
        KafkaTemplate<String, SharedCategoryEvent> kafkaTemplate = new KafkaTemplate<>(producerFactory());
        kafkaTemplate.setTransactionIdPrefix("category-events-");
        return kafkaTemplate;
    }

    @Bean
    public KafkaTransactionManager<String, SharedCategoryEvent> kafkaTransactionManager() {
        return new KafkaTransactionManager<>(producerFactory());
    }

    @Bean
    public InfrastructureCategoryEventPublisher infrastructureCategoryEventPublisher(KafkaTemplate<String, SharedCategoryEvent> kafkaTemplate) {
        return new InfrastructureCategoryEventPublisher(kafkaTemplate);
    }
}
