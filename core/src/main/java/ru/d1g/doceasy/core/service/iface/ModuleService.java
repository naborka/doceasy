package ru.d1g.doceasy.core.service.iface;

import ru.d1g.doceasy.postgres.model.Module;

import java.util.UUID;

public interface ModuleService {
    Module getById(UUID id);
}
