package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.utils.FileIOHelper;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class FileMessageRepository implements MessageRepository {

    private static final Path MESSAGE_DIRECTORY =
            FileIOHelper.resolveDirectory("messages");
    private final FileLockProvider fileLockProvider;

    public FileMessageRepository(FileLockProvider fileLockProvider) {
        this.fileLockProvider = fileLockProvider;
    }

    @Override
    public void save(Message message) {
        Path messageFilePath = MESSAGE_DIRECTORY.resolve(message.getId().toString());

        withLock(messageFilePath, () -> FileIOHelper.save(messageFilePath, message));
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Path messageFilePath = MESSAGE_DIRECTORY.resolve(id.toString());

        return withLock(messageFilePath, () -> FileIOHelper.load(messageFilePath));
    }

    @Override
    public List<Message> findAll() {
        return withLock(MESSAGE_DIRECTORY, () -> FileIOHelper.loadAll(MESSAGE_DIRECTORY));
    }

    @Override
    public void delete(Message message) {
        Path channelFilePath = MESSAGE_DIRECTORY.resolve(message.getId().toString());

        withLock(channelFilePath, () -> FileIOHelper.delete(channelFilePath));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return withLock(MESSAGE_DIRECTORY, () -> FileIOHelper.<Message>loadAll(MESSAGE_DIRECTORY).stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList());
    }

    @Override
    public Instant findLastMessageAtByChannelId(UUID channelId) {
        return withLock(MESSAGE_DIRECTORY, () -> FileIOHelper.<Message>loadAll(MESSAGE_DIRECTORY).stream()
                .filter(message ->
                        message.getChannelId().equals(channelId)
                )
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null));
    }

    @Override
    public Map<UUID, Instant> findLastMessageAtByChannelIds(List<UUID> channelIds) {
        return withLock(MESSAGE_DIRECTORY, () -> FileIOHelper.<Message>loadAll(MESSAGE_DIRECTORY).stream()
                .filter(message -> channelIds.contains(message.getChannelId()))
                .collect(
                        java.util.stream.Collectors.toMap(
                                Message::getChannelId,
                                Message::getCreatedAt,
                                (t1, t2) -> t1.isAfter(t2) ? t1 : t2
                        )
                ));
    }

    @Override
    public void deleteById(UUID messageId) {
        Path messagePath = MESSAGE_DIRECTORY.resolve(messageId.toString());
        withLock(messagePath, () -> FileIOHelper.delete(messagePath));
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
