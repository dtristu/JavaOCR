package org.dtristu.javaocr.usersecurity.service;

import org.dtristu.javaocr.usersecurity.dao.Account;
import org.dtristu.javaocr.usersecurity.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


import java.util.Optional;

public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> user = accountRepository.findByUsername(username);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new UsernameNotFoundException("Username not found!");
        }
    }
}