package com.example.DispacherOCRJava.Config.Mongo;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import static com.example.DispacherOCRJava.Config.Mongo.AccountMongoConfig.MONGO_TEMPLATE;

@Configuration
@EnableMongoRepositories(basePackages = "com.example.DispacherOCRJava.Repository.Account",
        mongoTemplateRef =MONGO_TEMPLATE)
public class AccountMongoConfig {
    protected static final String MONGO_TEMPLATE = "accountMongoTemplate";
}