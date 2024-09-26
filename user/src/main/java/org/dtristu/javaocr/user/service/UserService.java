package org.dtristu.javaocr.user.service;

import org.dtristu.javaocr.commons.dto.OCRTask;
import org.dtristu.javaocr.user.dao.Account;
import org.dtristu.javaocr.user.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    AccountRepository accountRepository;
    public void addTaskToUser(OCRTask ocrTask) {
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
