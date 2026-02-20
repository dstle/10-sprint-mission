package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.utils.FileIOHelper;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class FileBinaryContentRepository implements BinaryContentRepository {

    private static final Path BINARY_CONTENT_DIRECTORY =
            FileIOHelper.resolveDirectory("binaryContents");
    private final FileLockProvider fileLockProvider;

    public FileBinaryContentRepository(FileLockProvider fileLockProvider) {
        this.fileLockProvider = fileLockProvider;
    }

    @Override
    public List<byte[]> findAllImagesByIds(List<UUID> attachmentIds) {
        return attachmentIds.stream()
                .map(id -> BINARY_CONTENT_DIRECTORY.resolve(id.toString()))
                .map(path -> withLock(path, () -> FileIOHelper.<BinaryContent>load(path)))
                .flatMap(Optional::stream)
                .map(BinaryContent::getBytes)
                .toList();
    }

    @Override
    public List<BinaryContent> findAllByIds(List<UUID> ids) {
        return ids.stream()
                .map(id -> BINARY_CONTENT_DIRECTORY.resolve(id.toString()))
                .map(path -> withLock(path, () -> FileIOHelper.<BinaryContent>load(path)))
                .flatMap(Optional::stream)
                .toList();
    }

    @Override
    public void save(BinaryContent binaryContent) {
        Path path = BINARY_CONTENT_DIRECTORY.resolve(binaryContent.getId().toString());
        withLock(path, () -> FileIOHelper.save(path, binaryContent));
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        Path path = BINARY_CONTENT_DIRECTORY.resolve(id.toString());
        return withLock(path, () -> FileIOHelper.load(path));
    }

    @Override
    public void delete(BinaryContent binaryContent) {
        Path path = BINARY_CONTENT_DIRECTORY.resolve(binaryContent.getId().toString());
        withLock(path, () -> FileIOHelper.delete(path));
    }

    @Override
    public void deleteById(UUID profileId) {
        Path path = BINARY_CONTENT_DIRECTORY.resolve(profileId.toString());
        withLock(path, () -> FileIOHelper.delete(path));
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
