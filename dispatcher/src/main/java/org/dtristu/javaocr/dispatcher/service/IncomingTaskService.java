package org.dtristu.javaocr.dispatcher.service;

import org.dtristu.javaocr.commons.dto.OCRTask;
import org.dtristu.javaocr.dispatcher.repository.Account.Account;
import org.dtristu.javaocr.dispatcher.repository.Account.AccountRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

import java.util.Optional;
@Service
public class IncomingTaskService {
    protected static final Logger logger = LogManager.getLogger(IncomingTaskService.class);
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    FailedTasksService failedTasksService;
    @KafkaListener(topics = "${spring.kafka.topic-in-task}", groupId = "${spring.kafka.group-id}")
    public void receiveFishedTask(OCRTask ocrTask) {
        logger.trace("Received task in dispatcher {}", ocrTask.getDocumentId());
        ocrTask.addToLog("Received task in dispatcher service " + Instant.now());
        updateUser(ocrTask);

    }
    public void updateUser(OCRTask ocrTask){
        Optional<Account> accountOptional= accountRepository.findByUserName(ocrTask.getUserName());
        if (accountOptional.isPresent()){
            Account account=accountOptional.get();
            account.addToOcrTaskList(ocrTask);
            accountRepository.save(account);
        } else{
            throw new RuntimeException();
        }
    }
    @KafkaListener(topics = "${spring.kafka.topic-in-exception}", groupId = "${spring.kafka.group-id}")
    public void receiveUnfinishedTask(OCRTask ocrTask) {
        logger.trace("Received task with exception in dispatcher {}", ocrTask.getDocumentId());
        ocrTask.addToLog("Received task with exception in dispatcher service " + Instant.now());
        failedTasksService.addTaskToFailedTasks(ocrTask);
    }
}
