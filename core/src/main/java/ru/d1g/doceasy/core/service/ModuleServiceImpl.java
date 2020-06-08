package ru.d1g.doceasy.core.service;

import org.springframework.stereotype.Service;
import ru.d1g.doceasy.core.data.repository.jpa.ModuleRepository;
import ru.d1g.doceasy.core.service.iface.ModuleService;
import ru.d1g.doceasy.postgres.model.Module;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    @Override
    public Collection<Module> findAll() {
        return StreamSupport.stream(moduleRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    @Override
    public Module save(Module module) {
        return moduleRepository.save(module);
    }

    @Override
    public void delete(Module module) {
        moduleRepository.delete(module);
    }
}
