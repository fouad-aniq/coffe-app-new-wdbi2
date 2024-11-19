package ai.shreds.application.services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import ai.shreds.application.ports.ApplicationCategoryEventOutputPort;
import ai.shreds.domain.ports.DomainPortCategoryEvent;
import ai.shreds.shared.SharedCategoryEvent;
import ai.shreds.domain.entities.DomainEntityCategory;
import ai.shreds.application.utils.ApplicationCategoryMapper;
import java.util.UUID;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ApplicationCategoryEventPublisher implements ApplicationCategoryEventOutputPort {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationCategoryEventPublisher.class);

    private final DomainPortCategoryEvent domainPortCategoryEvent;
    private final ApplicationCategoryMapper applicationCategoryMapper;

    @Autowired
    public ApplicationCategoryEventPublisher(DomainPortCategoryEvent domainPortCategoryEvent,
                                             ApplicationCategoryMapper applicationCategoryMapper) {
        this.domainPortCategoryEvent = domainPortCategoryEvent;
        this.applicationCategoryMapper = applicationCategoryMapper;
    }

    @Override
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void publishCategoryCreatedEvent(SharedCategoryEvent event) {
        try {
            DomainEntityCategory domainCategory = applicationCategoryMapper.toDomain(event.getCategory());
            domainPortCategoryEvent.publishCategoryCreatedEvent(domainCategory);
        } catch (Exception e) {
            logger.error("Failed to publish category created event", e);
            throw e;
        }
    }

    @Override
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void publishCategoryUpdatedEvent(SharedCategoryEvent event) {
        try {
            DomainEntityCategory domainCategory = applicationCategoryMapper.toDomain(event.getCategory());
            domainPortCategoryEvent.publishCategoryUpdatedEvent(domainCategory);
        } catch (Exception e) {
            logger.error("Failed to publish category updated event", e);
            throw e;
        }
    }

    @Override
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void publishCategoryDeletedEvent(UUID categoryId) {
        try {
            domainPortCategoryEvent.publishCategoryDeletedEvent(categoryId);
        } catch (Exception e) {
            logger.error("Failed to publish category deleted event", e);
            throw e;
        }
    }
}