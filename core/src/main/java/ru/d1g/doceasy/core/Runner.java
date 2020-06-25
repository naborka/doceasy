package ru.d1g.doceasy.core;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.d1g.doceasy.core.data.repository.jpa.ModuleRepository;
import ru.d1g.doceasy.postgres.model.Module;

//@Component
public class Runner implements CommandLineRunner {
    private final ModuleRepository moduleRepository;

    public Runner(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (moduleRepository.count() < 2) {
            Module module = new Module();
            module.setName("QR-code API Remote");
            module.setUrl("https://api.qrserver.com/v1/read-qr-code/?fileurl=");
            moduleRepository.save(module);
        }
        System.out.println();
    }
}
