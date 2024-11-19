package ai.shreds.application.services;

import ai.shreds.application.ports.ApplicationCategoryEventOutputPort;
import ai.shreds.shared.SharedCategoryEvent;
import ai.shreds.shared.SharedCategoryDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;

import java.util.UUID;
import java.sql.Timestamp;
import java.time.Instant;

@Service
public class ApplicationCategoryEventPublisher implements ApplicationCategoryEventOutputPort {

    private static final String TOPIC = "category_events";
    private final KafkaTemplate<String, SharedCategoryEvent> kafkaTemplate;

    public ApplicationCategoryEventPublisher(KafkaTemplate<String, SharedCategoryEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Transactional
    public void publishCategoryCreatedEvent(SharedCategoryEvent event) {
        event.setEventType("category_created");
        event.setTimestamp(Timestamp.from(Instant.now()));
        sendMessage(event);
    }

    @Override
    @Transactional
    public void publishCategoryUpdatedEvent(SharedCategoryEvent event) {
        event.setEventType("category_updated");
        event.setTimestamp(Timestamp.from(Instant.now()));
        sendMessage(event);
    }

    @Override
    @Transactional
    public void publishCategoryDeletedEvent(UUID categoryId) {
        SharedCategoryDTO categoryDTO = new SharedCategoryDTO();
        categoryDTO.setId(categoryId);

        SharedCategoryEvent event = new SharedCategoryEvent();
        event.setEventType("category_deleted");
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
                    // Log success
                    System.out.println("Sent message=[" + event + "] with offset=[" + result.getRecordMetadata().offset() + "]");
                }

                @Override
                public void onFailure(Throwable ex) {
                    System.err.println("Unable to send message=[" + event + "] due to : " + ex.getMessage());
                    // Throw exception to trigger retry
                    throw new RuntimeException("Failed to send message to Kafka", ex);
                }
            });
            return true;
        });
    }
}
