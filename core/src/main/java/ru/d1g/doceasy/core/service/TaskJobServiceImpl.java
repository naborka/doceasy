package ru.d1g.doceasy.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.d1g.doceasy.core.data.repository.jpa.TaskJobRepository;
import ru.d1g.doceasy.core.event.TaskJobStartedEvent;
import ru.d1g.doceasy.core.service.iface.ImageService;
import ru.d1g.doceasy.core.service.iface.TaskJobService;
import ru.d1g.doceasy.postgres.model.Module;
import ru.d1g.doceasy.postgres.model.QTaskJob;
import ru.d1g.doceasy.postgres.model.TaskJob;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class TaskJobServiceImpl implements TaskJobService {
    private final static Logger log = LoggerFactory.getLogger(TaskJobServiceImpl.class);

    private final ApplicationEventPublisher eventPublisher;
    private final ImageService imageService;
    private final TaskJobRepository taskJobRepository;

    public TaskJobServiceImpl(ApplicationEventPublisher eventPublisher, ImageService imageService, TaskJobRepository taskJobRepository) {
        this.eventPublisher = eventPublisher;
        this.imageService = imageService;
        this.taskJobRepository = taskJobRepository;
    }

    @Override
    public TaskJob create(Module module, Collection<String> imageIds) {
        TaskJob taskJob = new TaskJob();
        taskJob.setModule(module);
        imageIds.forEach(imageId -> {
            if (imageService.exists(imageId)) {
                taskJob.getImageIds().add(imageId);
            }
        });
        return taskJobRepository.save(taskJob);
    }

    @Override
    public void run(TaskJob taskJob) {
        eventPublisher.publishEvent(new TaskJobStartedEvent(this, taskJob));
    }

    @Override
    public TaskJob getById(UUID id) {
        return taskJobRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public Collection<TaskJob> getByImageIds(Collection<String> imageIds) {
        return StreamSupport.stream(taskJobRepository.findAll(QTaskJob.taskJob.imageIds.any().in(imageIds)).spliterator(), false).collect(Collectors.toSet());
    }

    @Override
    public Collection<TaskJob> findAll() {
        return StreamSupport.stream(taskJobRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    @Override
    public TaskJob save(TaskJob taskJob) {
        return taskJobRepository.save(taskJob);
    }

    @Override
    public void delete(TaskJob taskJob) {
        taskJobRepository.delete(taskJob);
    }
}
