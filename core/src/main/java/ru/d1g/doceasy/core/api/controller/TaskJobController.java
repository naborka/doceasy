package ru.d1g.doceasy.core.api.controller;

import org.springframework.web.bind.annotation.*;
import ru.d1g.doceasy.core.Constants;
import ru.d1g.doceasy.core.service.iface.ModuleService;
import ru.d1g.doceasy.core.service.iface.TaskJobService;
import ru.d1g.doceasy.postgres.model.Module;
import ru.d1g.doceasy.postgres.model.TaskJob;

import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping(Constants.API_URL + "/tasks")
public class TaskJobController {
    private final TaskJobService taskJobService;
    private final ModuleService moduleService;

    public TaskJobController(TaskJobService taskJobService, ModuleService moduleService) {
        this.taskJobService = taskJobService;
        this.moduleService = moduleService;
    }

    @PostMapping(path = "/{moduleId}/")
    public UUID postTask(@PathVariable("moduleId") UUID moduleId, @RequestBody Collection<String> imageIds) {
        Module module = moduleService.getById(moduleId);
        return taskJobService.create(module, imageIds).getId();
    }

    @PostMapping(path = "/{id}/start")
    public void run(@PathVariable("id") UUID id) {
        TaskJob taskJob = taskJobService.getById(id);
        taskJobService.run(taskJob);
    }
}
