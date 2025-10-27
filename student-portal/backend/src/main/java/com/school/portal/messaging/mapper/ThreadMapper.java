package com.school.portal.messaging.mapper;

import com.school.portal.messaging.MessageEntity;
import com.school.portal.messaging.ThreadEntity;
import com.school.portal.messaging.dto.MessageDto;
import com.school.portal.messaging.dto.ThreadDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ThreadMapper {

    ThreadDto toDto(ThreadEntity entity);

    @Mapping(target = "senderId", source = "sender.id")
    MessageDto toDto(MessageEntity entity);
}
