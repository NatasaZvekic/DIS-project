package com.reader.reader.services;

import com.reader.reader.persistence.ReaderEntity;
import core.readers.Reader;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReaderMapper {
    @Mappings({})
    Reader entityToApi(ReaderEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    ReaderEntity apiToEntity(Reader api);

    List<Reader> entityListToApiList(List<ReaderEntity> entity);
    List<ReaderEntity> apiListToEntityList(List<Reader> api);
}
