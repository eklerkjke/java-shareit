package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public class UserMapper {

    // Преобразует объект User в объект UserDto.
    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    /* Обновляет поля объекта User на основе данных из UserDto.
       Если поля в UserDto отсутствуют, используются существующие значения из User. */
    public static User toUserOnUpdate(UpdateUserRequest userRequest, User user) {
        return User.builder()
                .id(userRequest.getId())
                .name(Optional.ofNullable(userRequest.getName()).orElse(user.getName()))
                .email(Optional.ofNullable(userRequest.getEmail()).orElse(user.getEmail()))
                .build();
    }

    // Преобразует объект UserDto в объект User.
    public static User toUser(UserDto userDto) {
        return User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }
}