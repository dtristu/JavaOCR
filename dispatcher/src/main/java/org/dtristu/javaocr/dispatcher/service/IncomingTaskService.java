package org.dtristu.javaocr.dispatcher.service;

import org.dtristu.javaocr.commons.dto.OCRTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;

import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class IncomingTaskService {
    RestClient defaultClient;
    public IncomingTaskService() {
        this.defaultClient = RestClient.create();
    }
    protected static final Logger logger = LogManager.getLogger(IncomingTaskService.class);
    @Autowired
    FailedTasksService failedTasksService;
    @Autowired
    OutgoingTaskService outgoingTaskService;

    @KafkaListener(topics = "${spring.kafka.topic-in-task}", groupId = "${spring.kafka.group-id}")
    public void receiveFishedTask(OCRTask ocrTask) {
        logger.trace("Received task in dispatcher {}", ocrTask.getDocumentId());
        ocrTask.addToLog("Received task in dispatcher service " + Instant.now());
        outgoingTaskService.publishTaskToUser(ocrTask);
    }

    @KafkaListener(topics = "${spring.kafka.topic-in-exception}", groupId = "${spring.kafka.group-id}")
    public void receiveUnfinishedTask(OCRTask ocrTask) {
        logger.trace("Received task with exception in dispatcher {}", ocrTask.getDocumentId());
        ocrTask.addToLog("Received task with exception in dispatcher service " + Instant.now());
        failedTasksService.addTaskToFailedTasks(ocrTask);
    }
}
