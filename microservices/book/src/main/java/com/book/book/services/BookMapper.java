package com.book.book.services;

import com.book.book.perstistence.BookEntity;
import core.book.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface BookMapper {
    @Mappings({})
    Book entityToApi(BookEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    BookEntity apiToEntity(Book api);
}
