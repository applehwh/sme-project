package com.example.demo.configuration;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;

import java.util.Arrays;

@Configuration
public class MongoReactiveClientFactory  extends AbstractReactiveMongoConfiguration {
    @Autowired
    private MongoProperties mongo;


    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        builder
                .credential(MongoCredential.createCredential(mongo.getUsername(), mongo.getDatabase(), mongo.getPassword()))
                .applyToClusterSettings(settings  -> {
                    settings.hosts(Arrays.asList(new ServerAddress(mongo.getHost(), mongo.getPort())));
                });
    }

    @Override
    protected String getDatabaseName() {
        return mongo.getDatabase();
    }
}
