package ru.d1g.doceasy.core.event.handler;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.d1g.doceasy.core.Constants;
import ru.d1g.doceasy.core.event.TaskJobStartedEvent;
import ru.d1g.doceasy.core.service.iface.ImageService;
import ru.d1g.doceasy.mongo.model.Image;
import ru.d1g.doceasy.postgres.model.Module;
import ru.d1g.doceasy.postgres.model.TaskJob;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class TaskJobStartedHandler implements ApplicationListener<TaskJobStartedEvent> {
    private static final Logger log = LoggerFactory.getLogger(TaskJobStartedHandler.class);

    @Value("${doceasy.server.host}")
    private String host;

    private final RestTemplate moduleClient;
    private final ImageService imageService;

    public TaskJobStartedHandler(RestTemplate moduleClient, ImageService imageService) {
        this.moduleClient = moduleClient;
        this.imageService = imageService;
    }

    @Async
    @Override
    public void onApplicationEvent(TaskJobStartedEvent taskJobStartedEvent) {
        TaskJob taskJob = taskJobStartedEvent.getTaskJob();
        Module module = taskJob.getModule();
        taskJob.getImageIds().forEach(imageId -> {
            Image image = imageService.getById(imageId);
            ParameterizedTypeReference<List<HashMap<String, Object>>> responseType =
                    new ParameterizedTypeReference<>() {
                    };

            TypeReference<HashMap<String, Object>> typeReference =
                    new TypeReference<>() {
                    };

            if (StringUtils.isNotBlank(image.getUrl())) {
                List<HashMap<String, Object>> result = moduleClient.exchange(module.getUrl() + "{url}", HttpMethod.GET, null, responseType, image.getUrl()).getBody();
                log.debug("resut {}", result);
            } else {
                List<HashMap<String, Object>> result = moduleClient.exchange(module.getUrl() + "{url}", HttpMethod.GET, null, responseType, URI.create(host + Constants.API_URL + "/" + imageId).toString()).getBody();
                log.debug("resut {}", result);
            }
        });
    }
}
