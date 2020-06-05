package ru.d1g.doceasy.core.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = {"ru.d1g.doceasy.core.data.repository.mongo"})
@EntityScan(basePackages = {"ru.d1g.doceasy.mongo"})
public class MongoConfiguration {
}
