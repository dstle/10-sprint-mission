package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.response.ApiException;
import com.sprint.mission.discodeit.response.ErrorCode;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ReadStatusService readStatusService;
    private final ChannelMapper channelMapper;

    @Override
    @Transactional
    public ChannelDto createPublicChannel(PublicChannelCreateRequest request) {
        Channel channel = Channel.buildPublic(
                request.name(),
                request.description()
        );

        channelRepository.save(channel);
        return channelMapper.toDto(channel);
    }

    @Override
    @Transactional
    public ChannelDto createPrivateChannel(PrivateChannelCreateRequest request) {
        Channel channel = Channel.buildPrivate();
        channelRepository.save(channel);

        readStatusService.createInitialReadStatuses(
                channel.getId(),
                request.participantIds(),
                Instant.now()
        );

        return channelMapper.toDto(channel);
    }

    @Override
    @Transactional(readOnly = true)
    public ChannelDto findChannelByChannelId(UUID channelId) {
        return channelMapper.toDto(getChannelOrThrow(channelId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChannelDto> findAllChannelsByUserId(UUID requesterId) {
        List<Channel> channels = channelRepository.findVisibleChannels(requesterId,
                ChannelType.PUBLIC, ChannelType.PRIVATE);

        return channels.stream()
                .map(channelMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ChannelDto updateChannelInfo(UUID channelId, PublicChannelUpdateRequest request) {
        Channel channel = getChannelOrThrow(channelId);
        validateChannelType(channel);

        channel.updateInfo(
                request.newName(),
                request.newDescription()
        );

        return channelMapper.toDto(channel);
    }

    @Override
    @Transactional
    public void deleteChannel(UUID channelId) {
        Channel channel = getChannelOrThrow(channelId);
        channelRepository.delete(channel);
    }

    private Channel getChannelOrThrow(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.CHANNEL_NOT_FOUND,
                        "채널을 찾을 수 없습니다 channelId: " + id));
    }

    private void validateChannelType(Channel channel) {
        if (channel.isPrivate()) {
            throw new ApiException(ErrorCode.CHANNEL_UPDATE_FORBIDDEN,
                    "PRIVATE 채널은 수정할 수 없습니다. channelId: " + channel.getId());
        }
    }
}
