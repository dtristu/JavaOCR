package org.dtristu.javaocr.dispatcher.config.Mongo;


import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mongodb")
public class MultipleMongoProperties {


    private MongoProperties file = new MongoProperties();

    public MongoProperties getFile() {
        return file;
    }

    public void setFile(MongoProperties file) {
        this.file = file;
    }
}