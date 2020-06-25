package ru.d1g.doceasy.core.service.iface;

import ru.d1g.doceasy.postgres.model.Module;
import ru.d1g.doceasy.postgres.model.TaskJob;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface TaskJobService {
    TaskJob create(Module module, Collection<String> imageIds);
    void run(TaskJob taskJob);
    TaskJob getById(UUID id);
    Collection<TaskJob> getByImageIds(Collection<String> imageIds);
    Collection<TaskJob> findAll();
    TaskJob save(TaskJob taskJob);
    void delete(TaskJob taskJob);
}
