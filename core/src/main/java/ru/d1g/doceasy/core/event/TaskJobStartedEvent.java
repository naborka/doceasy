package ru.d1g.doceasy.core.event;

import org.springframework.context.ApplicationEvent;
import ru.d1g.doceasy.postgres.model.TaskJob;

public class TaskJobStartedEvent extends ApplicationEvent {
    private final TaskJob taskJob;

    public TaskJobStartedEvent(Object source, TaskJob taskJob) {
        super(source);
        this.taskJob = taskJob;
    }

    public TaskJob getTaskJob() {
        return taskJob;
    }
}
