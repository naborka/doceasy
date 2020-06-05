package ru.d1g.doceasy.core.api.request;

import ru.d1g.doceasy.postgres.model.Module;

import java.util.Set;

public class TaskJobRequest {
    private Module module;
    private Set<String> imageIds;

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public Set<String> getImageIds() {
        return imageIds;
    }

    public void setImageIds(Set<String> imageIds) {
        this.imageIds = imageIds;
    }
}
