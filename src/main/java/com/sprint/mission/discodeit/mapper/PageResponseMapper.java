package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.base.BaseEntity;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface PageResponseMapper {
    default <T extends MessageResponseDto> PageResponse<T> fromSlice(Slice<T> slice){
        Instant nextCursor = null;

        if(!slice.getContent().isEmpty()) {
            nextCursor = slice.getContent().get(slice.getContent().size() - 1).createdAt();

        }

        return new PageResponse<T>(
                slice.getContent(),
                nextCursor,
                slice.getSize(),
                slice.hasNext(),
                (long) slice.getNumberOfElements()
        );
    }
    default <T extends BaseEntity> PageResponse<T> fromPage(Page<T> page){
        Instant nextCursor = null;

        if(!page.getContent().isEmpty()) {
            nextCursor = page.getContent().get(page.getContent().size() - 1).getCreatedAt();

        }

        return new PageResponse<T>(
                page.getContent(),
                nextCursor,
                page.getSize(),
                page.hasNext(),
                page.getTotalElements()
        );
    }
}
