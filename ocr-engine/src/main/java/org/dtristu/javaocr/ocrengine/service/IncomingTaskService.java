package org.dtristu.javaocr.ocrengine.service;

import org.dtristu.javaocr.commons.dto.OCRTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class IncomingTaskService {
    protected static final Logger logger = LogManager.getLogger(IncomingTaskService.class);
    @Autowired
    OCRService ocrService;
    @Autowired
    OutgoingTaskService outgoingTaskService;

    @KafkaListener(topics = "${spring.kafka.topic-in}", groupId = "${spring.kafka.group-id}")
    public void receiveTask(OCRTask ocrTask) {
        logger.trace("Received task in ocr {}", ocrTask.getDocumentId());
        ocrTask.addToLog("Received task in ocr service " + Instant.now());
        try {
            ocrService.doTask(ocrTask);
        } catch (Exception e) {
            ocrTask.addToLog(e.getMessage());
            outgoingTaskService.warn(ocrTask);
            throw new RuntimeException(e);
        }
    }

}
