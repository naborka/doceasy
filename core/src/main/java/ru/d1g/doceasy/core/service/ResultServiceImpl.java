package ru.d1g.doceasy.core.service;

import org.springframework.stereotype.Service;
import ru.d1g.doceasy.core.data.repository.mongo.ResultRepository;
import ru.d1g.doceasy.core.service.iface.ResultService;
import ru.d1g.doceasy.mongo.model.Result;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class ResultServiceImpl implements ResultService {
    private final ResultRepository resultRepository;

    public ResultServiceImpl(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    @Override
    public Result getById(String id) {
        return resultRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public List<Result> findAll() {
        return resultRepository.findAll();
    }

    @Override
    public Result save(Result result) {
        return resultRepository.save(result);
    }

    @Override
    public void remove(Result result) {
        resultRepository.delete(result);
    }
}
