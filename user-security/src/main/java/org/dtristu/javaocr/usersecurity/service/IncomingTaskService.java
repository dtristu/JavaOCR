package org.dtristu.javaocr.usersecurity.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dtristu.javaocr.commons.dto.OCRTask;
import org.dtristu.javaocr.usersecurity.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class IncomingTaskService {
    protected static final Logger logger = LogManager.getLogger(IncomingTaskService.class);
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    UserService userService;
    @KafkaListener(topics = "${spring.kafka.topic-in-task}", groupId = "${spring.kafka.group-id}")
    public void receiveFishedTask(OCRTask ocrTask) {
        logger.trace("Received task in dispatcher {}", ocrTask.getDocumentId());
        ocrTask.addToLog("Received task in dispatcher service " + Instant.now());
        userService.addOcrTaskToAccount(ocrTask);
    }

}
