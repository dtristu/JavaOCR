package com.example.DispacherOCRJava.Service;

import com.example.DispacherOCRJava.Repository.Account.Account;
import com.example.DispacherOCRJava.Repository.Account.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Optional;

public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            try {
                Optional<Account> user = accountRepository.findByUserName(username);
                String userName = user.get().getUserName();
                String password = user.get().getPassword();
                return new User(userName, password, new ArrayList<>());
            } catch (Exception e) {
                throw new UsernameNotFoundException(e.toString());
            }
    }
}
