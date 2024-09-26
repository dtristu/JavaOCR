package org.dtristu.javaocr.dispatcher.repository.Account;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;
@Repository
public interface AccountRepository extends MongoRepository<Account,String> {
    public Optional<Account> findByUserName(String userName);
}
