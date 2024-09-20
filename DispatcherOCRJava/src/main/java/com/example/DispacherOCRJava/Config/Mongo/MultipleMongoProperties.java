package com.example.DispacherOCRJava.Config.Mongo;


import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mongodb")
public class MultipleMongoProperties {

    private MongoProperties account = new MongoProperties();
    private MongoProperties file = new MongoProperties();

    public MongoProperties getAccount() {
        return account;
    }

    public void setAccount(MongoProperties account) {
        this.account = account;
    }

    public MongoProperties getFile() {
        return file;
    }

    public void setFile(MongoProperties file) {
        this.file = file;
    }
}