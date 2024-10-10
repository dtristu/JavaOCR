package org.dtristu.javaocr.usersecurity.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dtristu.javaocr.commons.dto.AccountDTO;
import org.dtristu.javaocr.commons.dto.OCRTask;
import org.dtristu.javaocr.usersecurity.dao.Account;
import org.dtristu.javaocr.usersecurity.dto.CreateAccountDTO;
import org.dtristu.javaocr.usersecurity.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class UserService {
    protected static final Logger logger = LogManager.getLogger(UserService.class);
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
    
    public void addOcrTaskToAccount(OCRTask ocrTask){
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
    public AccountDTO updateAccountOcrTasks (AccountDTO accountDTO,String token) throws Exception {
        Jwt jwt= jwtDecoder.decode(token);
        String userName = jwt.getClaimAsString("sub");
        Optional<Account> accountOptional= accountRepository.findByUsername(userName);
        if (accountOptional.isEmpty()){
            throw new Exception("Account not found!");
        }
        Account account= accountOptional.get();
        account.setOcrTaskList(accountDTO.getOcrTaskList());
        accountRepository.save(account);
        return accountDTO;
    }

    public Set<String> getAllFileIds() {
        List<Account> accounts = accountRepository.findAll();
        Set<String> fileIds = new HashSet<>();
        for(Account account:accounts){
            List<OCRTask> ocrTaskList = account.getOcrTaskList();
            for(OCRTask ocrTask:ocrTaskList){
                if(ocrTask.getRawImagesId()!=null){
                    fileIds.addAll(ocrTask.getRawImagesId());
                }
                if(ocrTask.getDocumentId()!=null){
                    fileIds.add(ocrTask.getDocumentId());
                }
                if(ocrTask.getMergedResult()!=null){
                fileIds.add(ocrTask.getMergedResult());
                }
            }
        }
        return fileIds;
    }
}
