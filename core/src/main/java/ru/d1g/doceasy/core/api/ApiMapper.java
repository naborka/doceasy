package ru.d1g.doceasy.core.api;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import ru.d1g.doceasy.core.api.dto.ImageDTO;
import ru.d1g.doceasy.mongo.model.Image;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public abstract class ApiMapper {
    public abstract ImageDTO map(Image image);
}
