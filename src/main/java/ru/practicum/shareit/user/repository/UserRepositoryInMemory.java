package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EmailDuplicationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class UserRepositoryInMemory implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private Long id = 1L;

    @Override
    public User createUser(User user) {
        checkDuplicationEmail(user.getEmail());

        user.setId(id);
        users.put(id++, user);
        log.info("Создан новый пользователь: {}",user);
        return user;
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return Optional.ofNullable(users.getOrDefault(userId, null));
    }

    @Override
    public User updateUserById(User user) {
        Long id = user.getId();
        String email = user.getEmail();
        User updateUser = users.getOrDefault(id, null);
        if (updateUser == null) {
            throw new NotFoundException("Не удалось найти пользователя с id: " + id);
        }

        if (!updateUser.getEmail().equals(email)) {
            checkDuplicationEmail(email);
        }

        updateUser.setName(user.getName());
        updateUser.setEmail(email);

        log.info("Пользователь с ID: {} успешно обновлен", id);
        return updateUser;
    }

    @Override
    public void deleteUserById(Long userId) {
        if (users.containsKey(userId)) {
            users.remove(userId);
            log.info("Пользователь с ID: {} успешно удален", userId);
        } else {
            throw new NotFoundException("Не удалось найти пользователя с id: " + userId);
        }
    }

    private void checkDuplicationEmail(String email) {
        if (users.values().stream()
                .anyMatch(o -> o.getEmail().equals(email))) {
            throw new EmailDuplicationException("Имейл уже используется");
        }
    }
}