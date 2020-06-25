package ru.d1g.doceasy.core.service.iface;

import ru.d1g.doceasy.mongo.model.Result;

import java.util.List;

public interface ResultService {
    Result getById(String id);
    List<Result> findAll();
    Result save(Result result);
    void remove(Result result);
}
