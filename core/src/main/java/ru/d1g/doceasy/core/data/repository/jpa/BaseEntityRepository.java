package ru.d1g.doceasy.core.data.repository.jpa;


import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.d1g.doceasy.postgres.model.BaseEntity;

import java.util.UUID;

public interface BaseEntityRepository<E extends BaseEntity> extends PagingAndSortingRepository<E, UUID>, QuerydslPredicateExecutor<E> {
}
