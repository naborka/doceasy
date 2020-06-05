package ru.d1g.doceasy.core.service;


import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import ru.d1g.doceasy.core.data.repository.mongo.ImageRepository;
import ru.d1g.doceasy.core.service.iface.ImageService;
import ru.d1g.doceasy.mongo.model.Image;
import ru.d1g.doceasy.mongo.model.QImage;

import javax.annotation.Nullable;
import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.Collection;

@Service
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;

    public ImageServiceImpl(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    @Nullable
    public Image getById(String uuid) {
        return imageRepository.findOne(QImage.image.id.eq(uuid)).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public Image saveImage(String name, String contentType, String url, byte[] bytes) throws IOException {
        Image image = null;
        String imageHash = null;

        if (StringUtils.isNotBlank(url)) {
            image = getByUrl(url);
        } else {
            if (bytes.length != 0) {
                imageHash = hash(new String(bytes));
                image = getByHash(imageHash);
            }
        }

        if (image == null) {
            image = new Image();
            image.setName(name);
            image.setType(contentType);
            image.setHash(imageHash);
            image.setUrl(url);
            image.setData(bytes);
            return imageRepository.save(image);
        }
        return image;
    }

    @Override
    public Image getByUrl(String url) {
        return imageRepository.findOne(QImage.image.url.eq(url)).orElse(null);
    }

    @Override
    public Image getByHash(String hash) {
        return imageRepository.findOne(QImage.image.hash.eq(hash)).orElse(null);
    }

    @Override
    public boolean exists(String imageId) {
        return imageRepository.exists(QImage.image.id.eq(imageId));
    }

    @Override
    public Collection<Image> getAll() {
        return imageRepository.findAll();
    }

    public static String hash(String content) {
        return DigestUtils.md5DigestAsHex(content.getBytes());
    }
}
