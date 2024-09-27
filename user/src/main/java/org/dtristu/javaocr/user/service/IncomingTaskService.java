package org.dtristu.javaocr.user.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dtristu.javaocr.commons.dto.OCRTask;
import org.dtristu.javaocr.user.dao.Account;
import org.dtristu.javaocr.user.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class IncomingTaskService {
    protected static final Logger logger = LogManager.getLogger(IncomingTaskService.class);
    @Autowired
    AccountRepository accountRepository;

    @KafkaListener(topics = "${spring.kafka.topic-in-task}", groupId = "${spring.kafka.group-id}")
    public void receiveFishedTask(OCRTask ocrTask) {
        logger.trace("Received task in dispatcher {}", ocrTask.getDocumentId());
        ocrTask.addToLog("Received task in dispatcher service " + Instant.now());
        updateAccount(ocrTask);
    }
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void updateAccount(OCRTask ocrTask){
        Optional<Account> optionalAccount = accountRepository.findByUserName(ocrTask.getUserName());
        if (optionalAccount.isEmpty()){
            throw new RuntimeException("User not found!");
        }
        Account account=optionalAccount.get();
        account.addToOcrTaskList(ocrTask);
        accountRepository.save(account);
    }
}
