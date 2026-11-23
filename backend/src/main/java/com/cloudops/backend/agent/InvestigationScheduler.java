package com.cloudops.backend.agent;

import com.cloudops.backend.entity.AnomalyEventEntity;
import com.cloudops.backend.enums.InvestigationStatus;
import com.cloudops.backend.repository.AnomalyEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "cloudops.investigation.enabled", havingValue = "true")
public class InvestigationScheduler {

    private static final Logger log = LoggerFactory.getLogger(InvestigationScheduler.class);

    private final AnomalyEventRepository eventRepository;
    private final CloudOpsAgentService agentService;

    public InvestigationScheduler(
            AnomalyEventRepository eventRepository,
            CloudOpsAgentService agentService
    ) {
        this.eventRepository = eventRepository;
        this.agentService = agentService;
    }

    @Scheduled(
            initialDelay = 10000,
            fixedDelayString = "${cloudops.investigation.interval-ms:30000}"
    )
    public void investigatePending() {
        List<AnomalyEventEntity> pending = eventRepository
                .findByInvestigationStatusOrderByDetectedAtAsc(InvestigationStatus.PENDING);

        for (AnomalyEventEntity event : pending) {
            investigate(event);
        }
    }

    private void investigate(AnomalyEventEntity event) {
        event.setInvestigationStatus(InvestigationStatus.IN_PROGRESS);
        eventRepository.save(event);
        log.info("Investigation started for event {} ({} {})",
                event.getId(), event.getFunctionName(), event.getWindowStart());

        try {
            String report = agentService.investigateEvent(event);
            event.markCompleted(report);
            eventRepository.save(event);
            log.info("Investigation completed for event {}:\n{}", event.getId(), report);
        } catch (Exception e) {
            event.markFailed(e.toString());
            eventRepository.save(event);
            log.error("Investigation failed for event {}: {}", event.getId(), e.getMessage());
        }
    }
}