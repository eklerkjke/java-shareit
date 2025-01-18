package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1L, "User name", "user.email@test.com");
    }

    @Test
    void createUser() {
        when(userRepository.save(any(User.class))).thenReturn(UserMapper.toUser(userDto));

        UserDto createdUser = userService.createUser(userDto);
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());
        assertEquals(userDto.getName(), createdUser.getName());
        assertEquals(userDto.getEmail(), createdUser.getEmail());
    }

    @Test
    void getUserById() {
        when(userRepository.save(any(User.class))).thenReturn(UserMapper.toUser(userDto));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(UserMapper.toUser(userDto)));

        UserDto createdUser = userService.createUser(userDto);
        UserDto foundUser = userService.getUserById(createdUser.getId());
        assertNotNull(foundUser);
        assertEquals(createdUser.getId(), foundUser.getId());
    }

    @Test
    void deleteUserById() {
        when(userRepository.save(any(User.class))).thenReturn(UserMapper.toUser(userDto));

        UserDto createdUser = userService.createUser(userDto);
        userService.deleteUserById(createdUser.getId());
        assertThrows(NotFoundException.class, () -> userService.getUserById(createdUser.getId()));
    }
}
