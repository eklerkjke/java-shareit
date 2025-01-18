package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.RequestIdNotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @InjectMocks
    private RequestServiceImpl itemRequestService;

    @Mock
    private RequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "User name", "user.email@test.com");

        itemRequestDto = new ItemRequestDto(1L, "Need a book", user, null, List.of());
        itemRequest = new ItemRequest(1L, "Need a book", user, null);
    }

    @Test
    void createItemRequest() {
        when(userService.getUserById(anyLong())).thenReturn(UserMapper.toUserDto(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto createdRequest = itemRequestService.createRequest(itemRequestDto, user.getId());
        assertNotNull(createdRequest);
        assertNotNull(createdRequest.getId());
        assertEquals(itemRequestDto.getDescription(), createdRequest.getDescription());
        assertEquals(itemRequestDto.getRequestor(), createdRequest.getRequestor());
    }

    @Test
    void getMyItemRequests() {
        when(itemRequestRepository.findByRequestor(anyLong())).thenReturn(List.of(itemRequest));

        List<ItemRequestDto> requests = itemRequestService.getRequests(user.getId());
        assertNotNull(requests);
        assertFalse(requests.isEmpty());
        assertEquals(itemRequest.getDescription(), requests.getFirst().getDescription());
    }

    @Test
    void getAllOtherItemRequests() {
        when(itemRequestRepository.findAll()).thenReturn(List.of(itemRequest));

        List<ItemRequestDto> requests = itemRequestService.getAllRequests();
        assertNotNull(requests);
        assertFalse(requests.isEmpty());
        assertEquals(itemRequest.getDescription(), requests.getFirst().getDescription());
    }

    @Test
    void getItemRequestById() {
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        ItemRequest request = itemRequestService.getRequestById(itemRequest.getId());
        assertNotNull(request);
        assertEquals(itemRequest.getDescription(), request.getDescription());
    }

    @Test
    void getItemRequestByIdNotFound() {
        assertThrows(RequestIdNotFoundException.class, () -> itemRequestService.getRequestById(999L));
    }
}
