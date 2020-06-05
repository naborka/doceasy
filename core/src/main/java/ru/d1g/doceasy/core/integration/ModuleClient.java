package ru.d1g.doceasy.core.integration;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.util.Map;

@FeignClient(name = "module-client", url = "/", configuration = ModuleClientConfiguration.class)
public interface ModuleClient {
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    Map<String, String> moduleCall(URI uri, @RequestParam URI imageUri);
}
