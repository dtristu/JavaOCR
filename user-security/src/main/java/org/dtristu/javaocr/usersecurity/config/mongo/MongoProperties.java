package org.dtristu.javaocr.usersecurity.config.mongo;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mongodb")
public class MongoProperties {
    private org.springframework.boot.autoconfigure.mongo.MongoProperties Account = new org.springframework.boot.autoconfigure.mongo.MongoProperties();

    public org.springframework.boot.autoconfigure.mongo.MongoProperties getAccount() {
        return Account;
    }

    public void setFile(org.springframework.boot.autoconfigure.mongo.MongoProperties file) {
        this.Account = file;
    }
}
