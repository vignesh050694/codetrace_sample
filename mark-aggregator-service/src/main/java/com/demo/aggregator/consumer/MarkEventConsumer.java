package com.demo.aggregator.consumer;

import com.demo.aggregator.dto.ArrearMarkUpdateEvent;
import com.demo.aggregator.dto.SemesterMarkEvent;
import com.demo.aggregator.service.MarkAggregatorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MarkEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(MarkEventConsumer.class);

    @Autowired
    private MarkAggregatorService markAggregatorService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "${kafka.topic.mark-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeMarkCreated(String message) {
        try {
            logger.info("Received mark-created event: {}", message);
            SemesterMarkEvent event = objectMapper.readValue(message, SemesterMarkEvent.class);
            markAggregatorService.processMarkEvent(
                    event.getStudentRollNumber(),
                    event.getSemester(),
                    event.getSubject(),
                    event.getMarks(),
                    "SEMESTER_MARK_CREATED"
            );
        } catch (Exception e) {
            logger.error("Error processing mark-created event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${kafka.topic.mark-updated}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeMarkUpdated(String message) {
        try {
            logger.info("Received mark-updated event: {}", message);
            SemesterMarkEvent event = objectMapper.readValue(message, SemesterMarkEvent.class);
            markAggregatorService.processMarkEvent(
                    event.getStudentRollNumber(),
                    event.getSemester(),
                    event.getSubject(),
                    event.getMarks(),
                    "SEMESTER_MARK_UPDATED"
            );
        } catch (Exception e) {
            logger.error("Error processing mark-updated event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${kafka.topic.arrear-mark-update}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeArrearMarkUpdate(String message) {
        try {
            logger.info("Received arrear-mark-update event: {}", message);
            ArrearMarkUpdateEvent event = objectMapper.readValue(message, ArrearMarkUpdateEvent.class);
            markAggregatorService.processMarkEvent(
                    event.getStudentRollNumber(),
                    event.getSemester(),
                    event.getSubject(),
                    event.getNewMarks(),
                    "ARREAR_MARK_UPDATED"
            );
        } catch (Exception e) {
            logger.error("Error processing arrear-mark-update event: {}", e.getMessage(), e);
        }
    }
}
