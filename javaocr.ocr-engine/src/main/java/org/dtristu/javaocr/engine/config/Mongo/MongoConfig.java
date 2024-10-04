package org.dtristu.javaocr.engine.config.Mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
@EnableConfigurationProperties(MongoProperties.class)
public class MongoConfig {
    @Autowired
    private MongoProperties mongoProperties;

    @Bean
    public MongoTemplate fileMongoTemplate() throws Exception {
        return new MongoTemplate(primaryFactory(this.mongoProperties.getFile()));
    }

    @Bean
    public MongoDatabaseFactory primaryFactory(final org.springframework.boot.autoconfigure.mongo.MongoProperties mongo) throws Exception {
        return new SimpleMongoClientDatabaseFactory(mongoProperties.getFile().getUri());
    }

}

