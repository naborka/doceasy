package ru.d1g.doceasy.core.data.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.d1g.doceasy.mongo.model.DocumentEntity;

public interface DocumentEntityRepository<E extends DocumentEntity> extends MongoRepository<E, String>, QuerydslPredicateExecutor<E> {
}
