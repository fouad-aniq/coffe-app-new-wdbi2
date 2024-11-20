package ai.shreds.application.services;

import ai.shreds.application.ports.ApplicationCategoryEventOutputPort;
import ai.shreds.shared.SharedCategoryDTO;
import ai.shreds.shared.SharedCategoryEvent;
import ai.shreds.shared.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

/**
 * Service responsible for publishing category events to Kafka.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationCategoryEventPublisher implements ApplicationCategoryEventOutputPort {

    private static final String TOPIC = "category_events";
    private final KafkaTemplate<String, SharedCategoryEvent> kafkaTemplate;

    @Override
    @Transactional
    public void publishCategoryCreatedEvent(SharedCategoryEvent event) {
        event.setEventType(EventType.CATEGORY_CREATED.getValue());
        event.setTimestamp(Timestamp.from(Instant.now()));
        sendMessage(event);
    }

    @Override
    @Transactional
    public void publishCategoryUpdatedEvent(SharedCategoryEvent event) {
        event.setEventType(EventType.CATEGORY_UPDATED.getValue());
        event.setTimestamp(Timestamp.from(Instant.now()));
        sendMessage(event);
    }

    @Override
    @Transactional
    public void publishCategoryDeletedEvent(UUID categoryId) {
        SharedCategoryDTO categoryDTO = new SharedCategoryDTO();
        categoryDTO.setId(categoryId);

        SharedCategoryEvent event = new SharedCategoryEvent();
        event.setEventType(EventType.CATEGORY_DELETED.getValue());
        event.setTimestamp(Timestamp.from(Instant.now()));
        event.setCategory(categoryDTO);

        sendMessage(event);
    }

    @Retryable(
        value = { Exception.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000)
    )
    private void sendMessage(SharedCategoryEvent event) {
        kafkaTemplate.executeInTransaction(operations -> {
            ListenableFuture<SendResult<String, SharedCategoryEvent>> future = operations.send(TOPIC, event);
            future.addCallback(new ListenableFutureCallback<SendResult<String, SharedCategoryEvent>>() {
                @Override
                public void onSuccess(SendResult<String, SharedCategoryEvent> result) {
                    log.info("Sent message=[{}] with offset=[{}]", event, result.getRecordMetadata().offset());
                }

                @Override
                public void onFailure(Throwable ex) {
                    log.error("Unable to send message=[{}] due to : {}", event, ex.getMessage());
                    throw new RuntimeException("Failed to send message to Kafka", ex);
                }
            });
            return null;
        });
    }
}