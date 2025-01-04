package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    UserDto createUser(UserDto user);

    UserDto getUserById(Long userId);

    UserDto updateUserById(UpdateUserRequest user);

    void deleteUserById(Long userId);
}