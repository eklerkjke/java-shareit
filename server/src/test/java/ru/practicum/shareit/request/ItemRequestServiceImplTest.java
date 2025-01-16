package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class ItemRequestServiceImplTest {

    @Autowired
    private RequestServiceImpl itemRequestService;

    @Autowired
    private RequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;
    private User user;

    @BeforeEach
    void setUp() {
        // Создание и сохранение пользователя
        user = new User(null, "User name", "user.email@test.com");
        user = userRepository.save(user);

        // Используем ID сохраненного пользователя
        itemRequestDto = new ItemRequestDto(null, "Need a book", user, null, List.of());
        itemRequest = new ItemRequest(null, "Need a book", user, null);
    }

    @Test
    void createItemRequest() {
        ItemRequestDto createdRequest = itemRequestService.createRequest(itemRequestDto, user.getId());
        assertNotNull(createdRequest);
        assertNotNull(createdRequest.getId());
        assertEquals(itemRequestDto.getDescription(), createdRequest.getDescription());
        assertEquals(itemRequestDto.getRequestor(), createdRequest.getRequestor());
    }

    @Test
    void getMyItemRequests() {
        itemRequestRepository.save(itemRequest);
        List<ItemRequestDto> requests = itemRequestService.getRequests(user.getId());
        assertNotNull(requests);
        assertFalse(requests.isEmpty());
        assertEquals(itemRequest.getDescription(), requests.getFirst().getDescription());
    }

    @Test
    void getAllOtherItemRequests() {
        itemRequestRepository.save(itemRequest);
        List<ItemRequestDto> requests = itemRequestService.getAllRequests();
        assertNotNull(requests);
        assertFalse(requests.isEmpty());
        assertEquals(itemRequest.getDescription(), requests.getFirst().getDescription());
    }

    @Test
    void getItemRequestById() {
        itemRequestRepository.save(itemRequest);
        ItemRequest request = itemRequestService.getRequestById(itemRequest.getId());
        assertNotNull(request);
        assertEquals(itemRequest.getDescription(), request.getDescription());
    }

    @Test
    void getItemRequestByIdNotFound() {
        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(999L));
    }
}
