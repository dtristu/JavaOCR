package org.dtristu.javaocr.user.service;

import org.dtristu.javaocr.commons.dto.AccountDTO;
import org.dtristu.javaocr.commons.dto.OCRTask;
import org.dtristu.javaocr.user.dao.Account;
import org.dtristu.javaocr.user.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    JwtDecoder jwtDecoder;
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
}
