package com.rate.rate.services;

import com.rate.rate.persistence.RateEntity;
import core.rates.Rate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RateMapper {
    @Mappings({})
    Rate entityToApi(RateEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    RateEntity apiToEntity(Rate api);

    List<Rate> entityListToApiList(List<RateEntity> entity);
    List<RateEntity> apiListToEntityList(List<Rate> api);
}

