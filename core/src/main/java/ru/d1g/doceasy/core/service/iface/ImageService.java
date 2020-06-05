package ru.d1g.doceasy.core.service.iface;

import ru.d1g.doceasy.mongo.model.Image;

import java.io.IOException;
import java.util.Collection;

public interface ImageService {
    Image getById(String uuid);
    Image saveImage(String name, String contentType, String url, byte[] bytes) throws IOException;
    Image getByUrl(String url);
    Image getByHash(String imageContent);
    boolean exists(String imageId);
    Collection<Image> getAll();
}
