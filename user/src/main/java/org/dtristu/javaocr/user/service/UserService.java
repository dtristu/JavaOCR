package org.dtristu.javaocr.user.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dtristu.javaocr.commons.dto.AccountDTO;
import org.dtristu.javaocr.commons.dto.OCRTask;
import org.dtristu.javaocr.user.dao.Account;
import org.dtristu.javaocr.user.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    protected static final Logger logger = LogManager.getLogger(IncomingTaskService.class);
    @Autowired
    JwtDecoder jwtDecoder;
    @Autowired
    AccountRepository accountRepository;
    private List<OCRTask> tasksToUpdate;
    public UserService() {
        tasksToUpdate=new ArrayList<>(5);
    }
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
    public Optional<AccountDTO> getAccount(String token){
        Jwt jwt= jwtDecoder.decode(token);
        String userName = jwt.getClaimAsString("sub");
        Optional<Account> accountOptional= accountRepository.findByUserName(userName);
        if (accountOptional.isPresent()) {
            Account account=accountOptional.get();
            AccountDTO accountDTO= new AccountDTO(account.getId(),account.getFirstName(),account.getLastName(),account.getUserName(),account.getOcrTaskList());
            return Optional.of(accountDTO);
        }
        return Optional.empty();
    }
    public void updateAccount(OCRTask ocrTask){
        tasksToUpdate.add(ocrTask);
        for(OCRTask task: tasksToUpdate){
            Optional<Account> optionalAccount = accountRepository.findByUserName(ocrTask.getUserName());
            if (optionalAccount.isEmpty()){
                throw new RuntimeException("User not found!");
            }
            Account account=optionalAccount.get();
            account.addToOcrTaskList(ocrTask);
            try {
                accountRepository.save(account);
            } catch (OptimisticLockingFailureException e){
                logger.warn("Could not update user");
                continue;
            }
            tasksToUpdate.remove(task);
        }
    }
}
