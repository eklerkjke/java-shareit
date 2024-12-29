package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto user) {
        log.info("Создание нового пользователя: {}", user);
        return UserMapper.toUserDto(userRepository.createUser(UserMapper.toUser(user)));
    }

    @Override
    public UserDto getUserById(Long userId) {
        log.info("Получение пользователя по ID: {}", userId);
        return UserMapper.toUserDto(findById(userId));
    }

    @Override
    public UserDto updateUserById(UpdateUserRequest user) {
        log.info("Обновление пользователя по ID: {}", user.getId());
        return UserMapper.toUserDto(userRepository
                .updateUserById(UserMapper.toUserOnUpdate(user, findById(user.getId()))));
    }

    @Override
    public void deleteUserById(Long userId) {
        log.info("Удаление пользователя по ID: {}", userId);
        userRepository.deleteUserById(userId);
    }

    private User findById(Long userId) {
        log.info("Поиск пользователя по ID: {}", userId);
        return userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id: " + userId));
    }
}