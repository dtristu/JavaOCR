package com.example.DispacherOCRJava.Repository.StoredFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

public class GridFsConfiguration extends AbstractMongoClientConfiguration {
    @Autowired
    private MappingMongoConverter mongoConverter;
    @Autowired
    @Qualifier("secondary")
    private MongoDatabaseFactory secondaryFactory;

    @Bean
    public GridFsTemplate gridFsTemplate() {
        return new GridFsTemplate(secondaryFactory, mongoConverter);
    }

    @Override
    protected String getDatabaseName() {
        return "StoredFile";
    }
}
