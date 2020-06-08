package ru.d1g.doceasy.core.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.d1g.doceasy.core.Constants;
import ru.d1g.doceasy.core.service.iface.ResultService;
import ru.d1g.doceasy.mongo.model.Result;

import java.util.List;

@RestController
@RequestMapping(Constants.API_URL + "/results")
public class ResultController {
    private final ResultService resultService;

    public ResultController(ResultService resultService) {
        this.resultService = resultService;
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable String id) {
        return resultService.getById(id);
    }

    @GetMapping
    public List<Result> getAll() {
        return resultService.findAll();
    }
}
