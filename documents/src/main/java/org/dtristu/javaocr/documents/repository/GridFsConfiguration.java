package org.dtristu.javaocr.documents.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

public class GridFsConfiguration extends AbstractMongoClientConfiguration {
    @Autowired
    private MappingMongoConverter mongoConverter;
    @Autowired
    private MongoDatabaseFactory databaseFactory;

    @Bean
    public GridFsTemplate gridFsTemplate() {
        return new GridFsTemplate(databaseFactory, mongoConverter);
    }

    @Override
    protected String getDatabaseName() {
        return "StoredFile";
    }
}
