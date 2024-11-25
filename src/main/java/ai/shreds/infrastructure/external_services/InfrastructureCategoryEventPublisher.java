package ai.shreds.infrastructure.external_services;

import ai.shreds.domain.entities.DomainEntityCategory;
import ai.shreds.domain.ports.DomainPortCategoryEvent;
import ai.shreds.infrastructure.exceptions.InfrastructureExceptionCategory;
import ai.shreds.shared.SharedCategoryDTO;
import ai.shreds.shared.SharedCategoryEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class InfrastructureCategoryEventPublisher implements DomainPortCategoryEvent {

    @Value(\"${kafka.topic.category-events}\")
    private String topic;

    private static final Logger logger = LoggerFactory.getLogger(InfrastructureCategoryEventPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String EVENT_TYPE_CREATED = \"category_created\";
    private static final String EVENT_TYPE_UPDATED = \"category_updated\";
    private static final String EVENT_TYPE_DELETED = \"category_deleted\";

    @Override
    public void publishCategoryCreatedEvent(DomainEntityCategory category) {
        SharedCategoryEvent event = createEvent(EVENT_TYPE_CREATED, category);
        sendEvent(event);
    }

    @Override
    public void publishCategoryUpdatedEvent(DomainEntityCategory category) {
        SharedCategoryEvent event = createEvent(EVENT_TYPE_UPDATED, category);
        sendEvent(event);
    }

    @Override
    public void publishCategoryDeletedEvent(UUID categoryId) {
        SharedCategoryDTO categoryDTO = SharedCategoryDTO.builder()
            .id(categoryId)
            .build();

        SharedCategoryEvent event = SharedCategoryEvent.builder()
            .eventType(EVENT_TYPE_DELETED)
            .timestamp(Timestamp.from(Instant.now()))
            .category(categoryDTO)
            .build();

        sendEvent(event);
    }

    private SharedCategoryEvent createEvent(String eventType, DomainEntityCategory category) {
        SharedCategoryDTO categoryDTO = mapToSharedCategoryDTO(category);

        return SharedCategoryEvent.builder()
            .eventType(eventType)
            .timestamp(Timestamp.from(Instant.now()))
            .category(categoryDTO)
            .build();
    }

    private void sendEvent(SharedCategoryEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);

            kafkaTemplate.executeInTransaction(operations -> {
                try {
                    operations.send(topic, message).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new KafkaException(\"Failed to send message\", e);
                }
                return true;
            });
        } catch (Exception e) {
            logger.error(\"Error sending event to Kafka: {}\", e.getMessage(), e);
            int retryCount = 3;
            for (int i = 0; i < retryCount; i++) {
                try {
                    kafkaTemplate.send(topic, objectMapper.writeValueAsString(event)).get();
                    return;
                } catch (Exception retryException) {
                    logger.warn(\"Retry {} failed to publish event to Kafka: {}\", i + 1, retryException.getMessage());
                    // Retry failed, continue to next attempt
                }
            }
            throw new InfrastructureExceptionCategory(\"Failed to publish event to Kafka after retries\", e);
        }
    }

    private SharedCategoryDTO mapToSharedCategoryDTO(DomainEntityCategory category) {
        return SharedCategoryDTO.builder()
            .id(category.getId())
            .name(category.getName())
            .description(category.getDescription())
            .parentCategoryId(
                category.getParentCategory() != null ? category.getParentCategory().getId() : null
            )
            .tags(category.getTags())
            .metadata(category.getMetadata())
            .createdAt(category.getCreatedAt())
            .updatedAt(category.getUpdatedAt())
            .build();
    }
}