package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public class UserMapper {

    // Преобразует объект User в объект UserDto.
    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    /* Обновляет поля объекта User на основе данных из UserDto.
       Если поля в UserDto отсутствуют, используются существующие значения из User. */
    public static User toUserOnUpdate(UpdateUserRequest userRequest, User user) {
        User newUser = new User();
        newUser.setId(userRequest.getId());
        newUser.setName(Optional.ofNullable(userRequest.getName()).orElse(user.getName()));
        newUser.setEmail(Optional.ofNullable(userRequest.getEmail()).orElse(user.getEmail()));
        return newUser;
    }

    // Преобразует объект UserDto в объект User.
    public static User toUser(UserDto userDto) {
        User newUser = new User();
        newUser.setId(userDto.getId());
        newUser.setName(userDto.getName());
        newUser.setEmail(userDto.getEmail());
        return newUser;
    }
}