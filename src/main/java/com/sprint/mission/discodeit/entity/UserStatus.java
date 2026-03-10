package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_statuses")
public class UserStatus extends BaseUpdatableEntity {

    private static final int ONLINE_TIMEOUT_SECONDS = 300;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "last_active_at", nullable = false)
    private Instant lastActiveAt;

    public UserStatus(
            User user,
            Instant lastActiveAt
    ) {
        this.user = user;
        this.lastActiveAt = lastActiveAt;
    }

    public void markActive() {
        this.lastActiveAt = Instant.now();
        markUpdated();
    }

    public void updateLastActiveAt(Instant newLastActiveAt) {
        if (newLastActiveAt != null) {
            this.lastActiveAt = newLastActiveAt;
            markUpdated();
        }
    }

    public UserOnlineStatus getOnlineStatus() {
        Instant threshold = Instant.now()
                .minusSeconds(ONLINE_TIMEOUT_SECONDS);

        return lastActiveAt.isAfter(threshold)
                ? UserOnlineStatus.ONLINE
                : UserOnlineStatus.OFFLINE;
    }

    public boolean isOnline() {
        return getOnlineStatus() == UserOnlineStatus.ONLINE;
    }
}
