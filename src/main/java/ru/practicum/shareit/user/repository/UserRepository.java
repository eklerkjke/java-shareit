package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserRepository {

    User createUser(User user);

    Optional<User> getUserById(Long userId);

    User updateUserById(User user);

    void deleteUserById(Long userId);
}