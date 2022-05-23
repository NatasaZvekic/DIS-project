package com.comment.comment.services;

import com.comment.comment.persistence.CommentEntity;
import core.comments.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mappings({})
    Comment entityToApi(CommentEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    CommentEntity apiToEntity(Comment api);

    List<Comment> entityListToApiList(List<CommentEntity> entity);
    List<CommentEntity> apiListToEntityList(List<Comment> api);
}
