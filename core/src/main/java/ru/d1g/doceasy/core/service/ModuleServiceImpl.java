package ru.d1g.doceasy.core.service;

import org.springframework.stereotype.Service;
import ru.d1g.doceasy.core.data.repository.jpa.ModuleRepository;
import ru.d1g.doceasy.core.service.iface.ModuleService;
import ru.d1g.doceasy.postgres.model.Module;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

@Service
public class ModuleServiceImpl implements ModuleService {
    private final ModuleRepository moduleRepository;

    public ModuleServiceImpl(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    @Override
    public Module getById(UUID id) {
        return moduleRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
