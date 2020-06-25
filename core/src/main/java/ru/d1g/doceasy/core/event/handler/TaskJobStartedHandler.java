package ru.d1g.doceasy.core.event.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.d1g.doceasy.core.Constants;
import ru.d1g.doceasy.core.event.TaskJobStartedEvent;
import ru.d1g.doceasy.core.service.iface.ImageService;
import ru.d1g.doceasy.core.service.iface.ResultService;
import ru.d1g.doceasy.mongo.model.Image;
import ru.d1g.doceasy.mongo.model.Result;
import ru.d1g.doceasy.postgres.model.Module;
import ru.d1g.doceasy.postgres.model.TaskJob;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TaskJobStartedHandler implements ApplicationListener<TaskJobStartedEvent> {
    private static final Logger log = LoggerFactory.getLogger(TaskJobStartedHandler.class);

    @Value("${doceasy.server.address}")
    private String serverAddress;

    private final RestTemplate moduleClient;
    private final ImageService imageService;
    private final ResultService resultService;

    public TaskJobStartedHandler(RestTemplate moduleClient, ImageService imageService, ResultService resultService) {
        this.moduleClient = moduleClient;
        this.imageService = imageService;
        this.resultService = resultService;
    }

    @Async
    @Override
    public void onApplicationEvent(TaskJobStartedEvent taskJobStartedEvent) {
        TaskJob taskJob = taskJobStartedEvent.getTaskJob();
        Module module = taskJob.getModule();
        taskJob.getImageIds().forEach(imageId -> {
            String imageUrl;
            try {
                URL url = new URL(imageId);
                imageUrl = url.toString();
            } catch (MalformedURLException e) {
                Image image = imageService.getById(imageId);
                imageUrl = image.getUrl();
            }
            ParameterizedTypeReference<List<Map<String, Object>>> responseType =
                    new ParameterizedTypeReference<>() {
                    };

            TypeReference<HashMap<String, Object>> typeReference =
                    new TypeReference<>() {
                    };
            List<Map<String, Object>> resultData;
            if (StringUtils.isNotBlank(imageUrl)) {
                resultData = moduleClient.exchange(module.getUrl() + "{url}", HttpMethod.GET, null, responseType, imageUrl).getBody();
                log.debug("resut {}", resultData);
            } else {
                resultData = moduleClient.exchange(module.getUrl() + "{url}", HttpMethod.GET, null, responseType, URI.create(serverAddress + Constants.API_URL + "/" + imageId).toString()).getBody();
                log.debug("resut {}", resultData);
            }

            Result result = new Result();
            result.setImageId(imageId);
            result.setData(resultData);
            resultService.save(result);
        });
    }
}
