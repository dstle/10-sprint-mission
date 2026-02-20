package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.utils.FileIOHelper;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class FileReadStatusRepository implements ReadStatusRepository {

    private static final Path READ_STATUS_DIRECTORY =
            FileIOHelper.resolveDirectory("read-status");
    private final FileLockProvider fileLockProvider;

    public FileReadStatusRepository(FileLockProvider fileLockProvider) {
        this.fileLockProvider = fileLockProvider;
    }

    @Override
    public void save(ReadStatus readStatus) {
        Path filePath = READ_STATUS_DIRECTORY.resolve(
                readStatus.getId().toString()
        );

        withLock(filePath, () -> FileIOHelper.save(filePath, readStatus));
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return withLock(READ_STATUS_DIRECTORY, () -> FileIOHelper.<ReadStatus>loadAll(READ_STATUS_DIRECTORY).stream()
                .filter(readStatus ->
                        readStatus.getChannelId().equals(channelId)
                )
                .toList());
    }

    @Override
    public void delete(ReadStatus readStatus) {
        Path filePath = READ_STATUS_DIRECTORY.resolve(
                readStatus.getId().toString()
        );

        withLock(filePath, () -> FileIOHelper.delete(filePath));
    }

    @Override
    public boolean existsByUserIdAndChannelId(UUID userId, UUID channelId) {
        return withLock(READ_STATUS_DIRECTORY, () -> FileIOHelper.<ReadStatus>loadAll(READ_STATUS_DIRECTORY).stream()
                .anyMatch(readStatus ->
                        readStatus.getUserId().equals(userId)
                                && readStatus.getChannelId().equals(channelId)
                ));
    }

    @Override
    public Optional<ReadStatus> findById(UUID readStatusId) {
        Path filePath = READ_STATUS_DIRECTORY.resolve(readStatusId.toString());
        return withLock(filePath, () -> FileIOHelper.load(filePath));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return withLock(READ_STATUS_DIRECTORY, () -> FileIOHelper.<ReadStatus>loadAll(READ_STATUS_DIRECTORY).stream()
                .filter(readStatus ->
                        readStatus.getUserId().equals(userId)
                )
                .toList());
    }

    @Override
    public void deleteById(UUID readStatusId) {
        Path filePath = READ_STATUS_DIRECTORY.resolve(readStatusId.toString());
        withLock(filePath, () -> FileIOHelper.delete(filePath));
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
