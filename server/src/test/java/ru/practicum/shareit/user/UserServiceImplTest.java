package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    private UserDto userDto;
    private UpdateUserRequest updateUserRequest;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(null, "User name", "user.email@test.com");
        updateUserRequest = new UpdateUserRequest(null, "user.email.updated@test.com", "",
                "User name Updated", null);
    }

    @Test
    void createUser() {
        UserDto createdUser = userService.createUser(userDto);
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());
        assertEquals(userDto.getName(), createdUser.getName());
        assertEquals(userDto.getEmail(), createdUser.getEmail());
    }

    @Test
    void getUserById() {
        UserDto createdUser = userService.createUser(userDto);
        UserDto foundUser = userService.getUserById(createdUser.getId());
        assertNotNull(foundUser);
        assertEquals(createdUser.getId(), foundUser.getId());
    }

    @Test
    void updateUserById() {
        UserDto createdUser = userService.createUser(userDto);
        updateUserRequest.setId(createdUser.getId());
        UserDto updatedUser = userService.updateUserById(updateUserRequest);
        assertNotNull(updatedUser);
        assertEquals(updateUserRequest.getName(), updatedUser.getName());
        assertEquals(updateUserRequest.getEmail(), updatedUser.getEmail());
    }

    @Test
    void deleteUserById() {
        UserDto createdUser = userService.createUser(userDto);
        userService.deleteUserById(createdUser.getId());
        assertThrows(NotFoundException.class, () -> userService.getUserById(createdUser.getId()));
    }
}
