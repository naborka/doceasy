package ru.d1g.doceasy.core.api.controller;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.d1g.doceasy.core.Constants;
import ru.d1g.doceasy.core.service.iface.ImageService;
import ru.d1g.doceasy.mongo.model.Image;

import java.io.IOException;
import java.util.Collection;

@RestController
@RequestMapping(Constants.API_URL + "/images")
public class ImageController {
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("/")
    public Collection<Image> getAll() {
        return imageService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getById(@PathVariable String id) {
        Image image = imageService.getById(id);
        if (image.getHash().isBlank()) {
            // TODO - load from remote server
            throw new RuntimeException("it's remote image");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(image.getType()))
                .body(image.getData());
    }

//    @GetMapping("/{id}/url")
//    public ResponseEntity<String> getUrlById(@PathVariable String id) {
//        Image image = imageService.getById(id);
//        return ResponseEntity.ok(image.getUrl());
//    }

    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(MultipartFile file) throws IOException {
        if (DataSize.ofBytes(file.getSize()).compareTo(DataSize.ofMegabytes(15)) > 0) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
        }
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
        }
        Image image = imageService.saveImage(file.getName(), file.getContentType(), null, file.getBytes());
        return ResponseEntity.ok(image.getId());
    }

    @PostMapping(value = "/uri")
    public ResponseEntity<String> uploadImage(@RequestBody String url) throws IOException {
//        TODO: check image size and other things
        Image image = imageService.saveImage("", "", url, null);
        return ResponseEntity.ok(image.getId());
    }
}
