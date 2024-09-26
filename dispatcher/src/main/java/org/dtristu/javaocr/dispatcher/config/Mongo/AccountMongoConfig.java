package org.dtristu.javaocr.dispatcher.config.Mongo;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import static org.dtristu.javaocr.dispatcher.config.Mongo.AccountMongoConfig.MONGO_TEMPLATE;

@Configuration
@EnableMongoRepositories(basePackages = "org/dtristu/javaocr/dispatcher/repository/Account",
        mongoTemplateRef =MONGO_TEMPLATE)
public class AccountMongoConfig {
    protected static final String MONGO_TEMPLATE = "accountMongoTemplate";
}