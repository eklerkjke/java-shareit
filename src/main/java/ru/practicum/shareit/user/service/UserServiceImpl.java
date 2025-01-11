package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
    public UserDto createUser(UserDto user) {
        log.info("Создание нового пользователя: {}", user);
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(user)));
    }

    @Override
    public UserDto getUserById(Long userId) {
        log.info("Получение пользователя по ID: {}", userId);
        return UserMapper.toUserDto(findById(userId));
    }

    @Override
    @Transactional
    public UserDto updateUserById(UpdateUserRequest user) {
        log.info("Обновление пользователя по ID: {}", user.getId());
        return UserMapper.toUserDto(userRepository
                .save(UserMapper.toUserOnUpdate(user, findById(user.getId()))));
    }

    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        log.info("Удаление пользователя по ID: {}", userId);
        userRepository.deleteById(userId);
    }

    private User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id: " + userId));
    }
}