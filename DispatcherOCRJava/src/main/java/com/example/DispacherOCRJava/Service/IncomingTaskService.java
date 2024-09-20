package com.example.DispacherOCRJava.Service;

import com.example.DispacherOCRJava.Repository.Account.Account;
import com.example.DispacherOCRJava.Repository.Account.AccountRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.example.LibraryOCRJava.OCRTask;
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
    @KafkaListener(topics = "${spring.kafka.topic-in}", groupId = "${spring.kafka.group-id}")
    public void receiveTask(OCRTask ocrTask) {
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
}
