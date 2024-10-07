package org.dtristu.javaocr.user.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dtristu.javaocr.commons.dto.AccountDTO;
import org.dtristu.javaocr.commons.dto.OCRTask;
import org.dtristu.javaocr.user.dao.Account;
import org.dtristu.javaocr.user.dto.CreateAccountDTO;
import org.dtristu.javaocr.user.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    protected static final Logger logger = LogManager.getLogger(IncomingTaskService.class);
    @Autowired
    JwtDecoder jwtDecoder;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    private List<OCRTask> tasksToUpdate;
    public UserService() {
        tasksToUpdate=new ArrayList<>(5);
    }

    public Optional<AccountDTO> getAccount(String token){
        Jwt jwt= jwtDecoder.decode(token);
        String userName = jwt.getClaimAsString("sub");
        Optional<Account> accountOptional= accountRepository.findByUsername(userName);
        if (accountOptional.isPresent()) {
            Account account=accountOptional.get();
            AccountDTO accountDTO= new AccountDTO(account.getId(),account.getFirstName(),account.getLastName(),account.getUsername(),account.getOcrTaskList());
            return Optional.of(accountDTO);
        }
        return Optional.empty();
    }
    
    public void updateAccount(OCRTask ocrTask){
        tasksToUpdate.add(ocrTask);
        Iterator<OCRTask> iterator = tasksToUpdate.iterator();
        while (iterator.hasNext()) {
            Optional<Account> optionalAccount = accountRepository.findByUsername(iterator.next().getUserName());
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
            iterator.remove();
        }
    }

    public AccountDTO createAccount(CreateAccountDTO createAccountDTO) throws Exception{
        Account account= new Account();
        account.setFirstName(createAccountDTO.getFirstName());
        account.setLastName(createAccountDTO.getLastName());
        account.setUsername(createAccountDTO.getUsername());
        account.setPassword(passwordEncoder.encode(createAccountDTO.getPassword()));
        account.setCreatedDate(LocalDate.now());
        account.setAuthorities(List.of((new SimpleGrantedAuthority("user"))));
        accountRepository.save(account);
        return new AccountDTO(account.getId(),account.getFirstName(),account.getLastName(),account.getUsername(),account.getOcrTaskList());
    }
}
