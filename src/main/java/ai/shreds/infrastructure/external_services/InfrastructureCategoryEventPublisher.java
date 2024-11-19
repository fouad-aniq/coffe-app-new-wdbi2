package ai.shreds.infrastructure.external_services;

import ai.shreds.domain.entities.DomainEntityCategory;
import ai.shreds.domain.ports.DomainPortCategoryEvent;
import ai.shreds.shared.SharedCategoryDTO;
import ai.shreds.shared.SharedCategoryEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.RetriableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.time.Instant;
import java.sql.Timestamp;
import java.util.UUID;

@Service
public class InfrastructureCategoryEventPublisher implements DomainPortCategoryEvent {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.category_events}")
    private String categoryEventsTopic;

    public InfrastructureCategoryEventPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    @Transactional
    public void publishCategoryCreatedEvent(DomainEntityCategory category) {
        publishEvent(category, "category_created");
    }

    @Override
    @Transactional
    public void publishCategoryUpdatedEvent(DomainEntityCategory category) {
        publishEvent(category, "category_updated");
    }

    @Override
    @Transactional
    public void publishCategoryDeletedEvent(UUID categoryId) {
        SharedCategoryEvent event = new SharedCategoryEvent();
        event.setEventType("category_deleted");
        event.setTimestamp(Timestamp.from(Instant.now()));
        SharedCategoryDTO categoryDTO = new SharedCategoryDTO();
        categoryDTO.setId(categoryId);
        event.setCategory(categoryDTO);
        sendMessage(event, categoryId.toString());
    }

    private void publishEvent(DomainEntityCategory category, String eventType) {
        SharedCategoryEvent event = new SharedCategoryEvent();
        event.setEventType(eventType);
        event.setTimestamp(Timestamp.from(Instant.now()));
        SharedCategoryDTO categoryDTO = mapToDTO(category);
        event.setCategory(categoryDTO);
        sendMessage(event, category.getId().toString());
    }

    private SharedCategoryDTO mapToDTO(DomainEntityCategory category) {
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

    private void sendMessage(SharedCategoryEvent event, String key) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.executeInTransaction(operations -> {
                operations.send(categoryEventsTopic, key, message).addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                    @Override
                    public void onSuccess(SendResult<String, String> result) {
                        // Handle success if necessary
                    }

                    @Override
                    public void onFailure(Throwable ex) {
                        if (ex instanceof RetriableException) {
                            // Implement retry logic or handle retriable exceptions
                        } else {
                            // Non-retriable exception, handle accordingly
                            throw new RuntimeException("Failed to send message to Kafka", ex);
                        }
                    }
                });
                return null;
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event message", e);
        }
    }
}
