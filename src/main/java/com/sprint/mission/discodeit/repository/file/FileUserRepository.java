package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.utils.FileIOHelper;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class FileUserRepository implements UserRepository {

    private static final Path USER_DIRECTORY =
            FileIOHelper.resolveDirectory("users");
    private final FileLockProvider fileLockProvider;

    public FileUserRepository(FileLockProvider fileLockProvider) {
        this.fileLockProvider = fileLockProvider;
    }

    @Override
    public void save(User user) {
        Path userFilePath = USER_DIRECTORY.resolve(user.getId().toString());

        withLock(userFilePath, () -> FileIOHelper.save(userFilePath, user));
    }

    @Override
    public Optional<User> findById(UUID id) {
        Path userFilePath = USER_DIRECTORY.resolve(id.toString());

        return withLock(userFilePath, () -> FileIOHelper.load(userFilePath));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return withLock(USER_DIRECTORY, () -> FileIOHelper.<User>loadAll(USER_DIRECTORY).stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst());
    }

    @Override
    public List<User> findAll() {
        return withLock(USER_DIRECTORY, () -> FileIOHelper.loadAll(USER_DIRECTORY));
    }

    @Override
    public void delete(User user) {
        Path userFilePath = USER_DIRECTORY.resolve(user.getId().toString());

        withLock(userFilePath, () -> FileIOHelper.delete(userFilePath));
    }

    @Override
    public boolean existsById(UUID id) {
        Path userFilePath = USER_DIRECTORY.resolve(id.toString());

        return withLock(userFilePath, () -> FileIOHelper.exists(userFilePath));
    }

    @Override
    public boolean existsByUsername(String username) {
        return withLock(USER_DIRECTORY, () -> FileIOHelper.<User>loadAll(USER_DIRECTORY).stream()
                .anyMatch(user -> user.getUsername().equals(username)));
    }

    @Override
    public boolean existsByEmail(String email) {
        return withLock(USER_DIRECTORY, () -> FileIOHelper.<User>loadAll(USER_DIRECTORY).stream()
                .anyMatch(user -> user.getEmail().equals(email)));
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
