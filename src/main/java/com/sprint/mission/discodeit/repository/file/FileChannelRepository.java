package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.utils.FileIOHelper;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class FileChannelRepository implements ChannelRepository {

    private static final Path CHANNEL_DIRECTORY =
            FileIOHelper.resolveDirectory("channels");
    private final FileLockProvider fileLockProvider;

    public FileChannelRepository(FileLockProvider fileLockProvider) {
        this.fileLockProvider = fileLockProvider;
    }

    @Override
    public void save(Channel channel) {
        Path channelFilePath = CHANNEL_DIRECTORY.resolve(channel.getId().toString());

        withLock(channelFilePath, () -> FileIOHelper.save(channelFilePath, channel));
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        Path channelFilePath = CHANNEL_DIRECTORY.resolve(id.toString());

        return withLock(channelFilePath, () -> FileIOHelper.load(channelFilePath));
    }

    @Override
    public void delete(Channel channel) {
        Path channelFilePath = CHANNEL_DIRECTORY.resolve(channel.getId().toString());

        withLock(channelFilePath, () -> FileIOHelper.delete(channelFilePath));
    }

    @Override
    public List<Channel> findVisibleChannels(UUID requesterId) {
        return withLock(CHANNEL_DIRECTORY, () -> FileIOHelper.<Channel>loadAll(CHANNEL_DIRECTORY).stream()
                .filter(channel -> channel.isPublic()
                        || (channel.isPrivate() && channel.hasMember(requesterId)))
                .toList());
    }

    @Override
    public boolean existsById(UUID channelId) {
        Path channelFilePath = CHANNEL_DIRECTORY.resolve(channelId.toString());

        return withLock(channelFilePath, () -> FileIOHelper.exists(channelFilePath));
    }

    private void withLock(Path path, Runnable action) {
        ReentrantLock lock = fileLockProvider.getLock(path);
        lock.lock();
        try {
            action.run();
        } finally {
            lock.unlock();
        }
    }

    private <T> T withLock(Path path, Supplier<T> action) {
        ReentrantLock lock = fileLockProvider.getLock(path);
        lock.lock();
        try {
            return action.get();
        } finally {
            lock.unlock();
        }
    }
}
