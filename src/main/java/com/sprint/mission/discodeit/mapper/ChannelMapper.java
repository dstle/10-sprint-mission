package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.time.Instant;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ChannelMapper {

    @Autowired
    protected MessageRepository messageRepository;

    @Autowired
    protected ReadStatusRepository readStatusRepository;

    @Autowired
    protected UserMapper userMapper;

    @Mapping(target = "participants", expression = "java(mapParticipants(channel))")
    @Mapping(target = "lastMessageAt", expression = "java(mapLastMessageAt(channel))")
    public abstract ChannelDto toDto(Channel channel);

    protected List<UserDto> mapParticipants(Channel channel) {
        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelId(channel.getId());
        return readStatuses.stream()
                .map(ReadStatus::getUser)
                .map(userMapper::toDto)
                .distinct()
                .toList();
    }

    protected Instant mapLastMessageAt(Channel channel) {
        return messageRepository.findLastMessageAtByChannelId(channel.getId());
    }
}
