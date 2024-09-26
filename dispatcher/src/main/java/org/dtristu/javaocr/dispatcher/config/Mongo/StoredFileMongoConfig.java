package org.dtristu.javaocr.dispatcher.config.Mongo;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import static org.dtristu.javaocr.dispatcher.config.Mongo.StoredFileMongoConfig.MONGO_TEMPLATE;

@Configuration
@EnableMongoRepositories(basePackages = "org/dtristu/javaocr/dispatcher/repository/StoredFile",
        mongoTemplateRef = MONGO_TEMPLATE)
public class StoredFileMongoConfig {
    protected static final String MONGO_TEMPLATE = "storedFileMongoTemplate";
}