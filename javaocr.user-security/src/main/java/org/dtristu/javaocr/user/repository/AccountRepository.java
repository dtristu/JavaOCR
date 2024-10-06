package org.dtristu.javaocr.user.repository;

import org.dtristu.javaocr.user.dao.Account;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends MongoRepository<Account,String> {
    public Optional<Account> findByUsername(String userName);
}
