package org.dtristu.javaocr.user.config.mongo;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.dtristu.javaocr.user.dao.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.index.Index;

@Configuration
@EnableConfigurationProperties(MongoProperties.class)
public class MongoConfig {
    @Autowired
    private MongoProperties mongoProperties;

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(primaryFactory(this.mongoProperties.getAccount()));
    }

    @Bean
    public MongoDatabaseFactory primaryFactory(final org.springframework.boot.autoconfigure.mongo.MongoProperties mongo) throws Exception {
        return new SimpleMongoClientDatabaseFactory(mongoProperties.getAccount().getUri());
    }

    @Bean
    public MongoCollection<Document> createCollection(){
        try {
           MongoCollection<Document> collection = mongoTemplate().createCollection("User");
            mongoTemplate().indexOps(Account.class).ensureIndex(new Index("username", Sort.Direction.ASC).unique());
            return collection;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}