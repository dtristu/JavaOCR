package org.dtristu.javaocr.converter.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dtristu.javaocr.commons.dto.OCRTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class IncomingTaskService {
    protected static final Logger logger = LogManager.getLogger(IncomingTaskService.class);
    @Autowired
    PDFToImageConverter pdfToImageConverter;
    @KafkaListener(topics = "${spring.kafka.topic-in}", groupId = "${spring.kafka.group-id}")
    public void receiveTask(OCRTask ocrTask) {
        logger.trace("Received task in converter {}", ocrTask.getDocumentId());
        ocrTask.addToLog("Received task in converter service " + Instant.now());
        try {
            pdfToImageConverter.documentHandler(ocrTask);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
