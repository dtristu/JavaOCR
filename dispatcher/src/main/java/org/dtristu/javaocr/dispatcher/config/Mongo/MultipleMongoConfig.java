package org.dtristu.javaocr.dispatcher.config.Mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
@EnableConfigurationProperties(MultipleMongoProperties.class)
public class MultipleMongoConfig {
    @Autowired
    private final MultipleMongoProperties mongoProperties;


    @Bean(name = AccountMongoConfig.MONGO_TEMPLATE)
    public MongoTemplate acountMongoTemplate() throws Exception {
        return new MongoTemplate(primaryFactory(this.mongoProperties.getAccount()));
    }
    @Primary
    @Bean(name = StoredFileMongoConfig.MONGO_TEMPLATE)
    public MongoTemplate storedFileMongoTemplate() throws Exception {
        return new MongoTemplate(secondaryFactory(this.mongoProperties.getFile()));
    }

    @Bean
    public MongoDatabaseFactory primaryFactory(final MongoProperties mongo) throws Exception {
        return new SimpleMongoClientDatabaseFactory(mongoProperties.getAccount().getUri());
    }

    @Bean
    @Primary
    @Qualifier("secondary")
    public MongoDatabaseFactory secondaryFactory(final MongoProperties mongo) throws Exception {
        return new SimpleMongoClientDatabaseFactory(mongoProperties.getFile().getUri());
    }

    public MultipleMongoConfig(MultipleMongoProperties mongoProperties) {
        this.mongoProperties = mongoProperties;
    }
}