package org.dtristu.javaocr.usersecurity.repository;

import org.dtristu.javaocr.usersecurity.dao.Account;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends MongoRepository<Account,String> {
    public Optional<Account> findByUsername(String userName);
}
