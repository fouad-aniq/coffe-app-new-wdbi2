package ai.shreds.infrastructure.external_services;

import ai.shreds.domain.entities.DomainEntityCategory;
import ai.shreds.domain.ports.DomainPortCategoryEvent;
import ai.shreds.infrastructure.exceptions.InfrastructureExceptionCategory;
import ai.shreds.shared.SharedCategoryDTO;
import ai.shreds.shared.SharedCategoryEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class InfrastructureCategoryEventPublisher implements DomainPortCategoryEvent {

    private static final String TOPIC = "category_events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public InfrastructureCategoryEventPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();

        // Enable transactions on KafkaTemplate
        this.kafkaTemplate.setTransactionIdPrefix("categoryEvents-");
    }

    @Override
    @Transactional
    public void publishCategoryCreatedEvent(DomainEntityCategory category) throws InfrastructureExceptionCategory {
        SharedCategoryEvent event = createEvent("category_created", category);
        sendEvent(event);
    }

    @Override
    @Transactional
    public void publishCategoryUpdatedEvent(DomainEntityCategory category) throws InfrastructureExceptionCategory {
        SharedCategoryEvent event = createEvent("category_updated", category);
        sendEvent(event);
    }

    @Override
    @Transactional
    public void publishCategoryDeletedEvent(UUID categoryId) throws InfrastructureExceptionCategory {
        SharedCategoryDTO categoryDTO = new SharedCategoryDTO();
        categoryDTO.setId(categoryId);

        SharedCategoryEvent event = new SharedCategoryEvent();
        event.setEventType("category_deleted");
        event.setTimestamp(Timestamp.from(Instant.now()));
        event.setCategory(categoryDTO);

        sendEvent(event);
    }

    private SharedCategoryEvent createEvent(String eventType, DomainEntityCategory category) {
        SharedCategoryDTO categoryDTO = mapToSharedCategoryDTO(category);

        SharedCategoryEvent event = new SharedCategoryEvent();
        event.setEventType(eventType);
        event.setTimestamp(Timestamp.from(Instant.now()));
        event.setCategory(categoryDTO);

        return event;
    }

    private void sendEvent(SharedCategoryEvent event) throws InfrastructureExceptionCategory {
        try {
            String message = objectMapper.writeValueAsString(event);

            kafkaTemplate.executeInTransaction(operations -> {
                try {
                    operations.send(TOPIC, message).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new KafkaException("Failed to send message", e);
                }
                return true;
            });
        } catch (Exception e) {
            int retryCount = 3;
            for (int i = 0; i < retryCount; i++) {
                try {
                    kafkaTemplate.send(TOPIC, objectMapper.writeValueAsString(event)).get();
                    return;
                } catch (Exception retryException) {
                    // Retry failed, continue to next attempt
                }
            }
            throw new InfrastructureExceptionCategory("Failed to publish event to Kafka after retries", e);
        }
    }

    private SharedCategoryDTO mapToSharedCategoryDTO(DomainEntityCategory category) {
        SharedCategoryDTO dto = new SharedCategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setParentCategoryId(
            category.getParentCategory() != null ? category.getParentCategory().getId() : null
        );
        dto.setTags(category.getTags());
        dto.setMetadata(category.getMetadata());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        return dto;
    }
}
