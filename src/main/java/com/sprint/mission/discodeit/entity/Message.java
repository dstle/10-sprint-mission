package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import com.sprint.mission.discodeit.response.ErrorCode;
import com.sprint.mission.discodeit.response.ApiException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "messages")
public class Message extends BaseUpdatableEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @ManyToMany
    @JoinTable(
            name = "message_attachments",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "attachment_id")
    )
    private List<BinaryContent> attachments;

    public Message(User author, Channel channel, String content) {
        this.author = author;
        this.channel = channel;
        this.content = content;
        this.attachments = new ArrayList<>();
    }

    public void updateAttachments(List<BinaryContent> attachments) {
        this.attachments = attachments;
        markUpdated();
    }

    public void updateContent(String content) {
        Optional.ofNullable(content)
                .ifPresent(value -> this.content = value);
        markUpdated();
    }

    public void validateSender(UUID userId) {
        if (author == null || !author.getId().equals(userId)) {
            throw new ApiException(ErrorCode.MESSAGE_SENDER_MISMATCH,
                    "메세지의 sender가 아닙니다. userId: " + userId);
        }
    }
}
