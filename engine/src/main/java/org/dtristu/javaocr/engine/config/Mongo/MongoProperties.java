package org.dtristu.javaocr.engine.config.Mongo;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mongodb")
public class MongoProperties {
    private org.springframework.boot.autoconfigure.mongo.MongoProperties file = new org.springframework.boot.autoconfigure.mongo.MongoProperties();

    public org.springframework.boot.autoconfigure.mongo.MongoProperties getFile() {
        return file;
    }

    public void setFile(org.springframework.boot.autoconfigure.mongo.MongoProperties file) {
        this.file = file;
    }
}
