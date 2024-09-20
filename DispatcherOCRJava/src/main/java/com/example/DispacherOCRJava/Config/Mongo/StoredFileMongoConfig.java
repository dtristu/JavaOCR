package com.example.DispacherOCRJava.Config.Mongo;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import static com.example.DispacherOCRJava.Config.Mongo.StoredFileMongoConfig.MONGO_TEMPLATE;

@Configuration
@EnableMongoRepositories(basePackages = "com.example.DispacherOCRJava.Repository.StoredFile",
        mongoTemplateRef = MONGO_TEMPLATE)
public class StoredFileMongoConfig {
    protected static final String MONGO_TEMPLATE = "storedFileMongoTemplate";
}