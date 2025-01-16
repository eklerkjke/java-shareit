package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    private User user;
    private Item item;
    private ItemDto itemDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "User name", "user.email@test.com");

        item = new Item(1L, "Item Name", "Item Description", true, user.getId(), null);

        itemDto = new ItemDto(1L, "Item Name", "Item Description", true, user.getId(), null, null);
        commentDto = new CommentDto(1L, "Comment text", null, user.getName(), null);

        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                item, user, BookingStatus.APPROVED);
    }

    @Test
    void createItem() {
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto createdItem = itemService.createItem(itemDto);
        assertNotNull(createdItem);
        assertNotNull(createdItem.getId());
        assertEquals(itemDto.getName(), createdItem.getName());
        assertEquals(itemDto.getDescription(), createdItem.getDescription());
    }

    @Test
    void getAllItemByOwnerId() {
        when(itemRepository.findByOwnerId(anyLong())).thenReturn(List.of(item));

        List<ItemDtoBooking> items = itemService.getAllItemByOwnerId(user.getId());
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(item.getName(), items.getFirst().getName());
    }

    @Test
    void getItemById() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ItemDtoBooking foundItem = itemService.getItemById(item.getId());
        assertNotNull(foundItem);
        assertEquals(item.getName(), foundItem.getName());
    }

    @Test
    void updateItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        itemDto.setId(item.getId());
        itemDto.setName("Updated Item Name");

        when(itemRepository.save(any(Item.class))).thenReturn(ItemMapper.toItem(itemDto));

        ItemDto updatedItem = itemService.updateItem(itemDto);
        assertNotNull(updatedItem);
        assertEquals("Updated Item Name", updatedItem.getName());
    }

    @Test
    void deleteItemByItemId() {
        itemService.deleteItemByItemId(item.getId());
        assertThrows(NotFoundException.class, () -> itemService.getItemById(item.getId()));
    }

    @Test
    void searchItemsByText() {
        when(itemRepository.findByText(anyString())).thenReturn(List.of(item));

        List<ItemDto> items = itemService.searchItemsByText("Item Name");
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(item.getName(), items.getFirst().getName());
    }

    @Test
    void createComment() {
        Comment comment = CommentMapper.toComment(commentDto, item, user);
        comment.setId(1L);

        when(bookingRepository.checkHaveBooking(anyLong(), anyLong())).thenReturn(1);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(userService.getUserById(anyLong())).thenReturn(UserMapper.toUserDto(user));

        CommentDto createdComment = itemService.createComment(commentDto, user.getId(), item.getId());
        assertNotNull(createdComment);
        assertNotNull(createdComment.getId());
        assertEquals(commentDto.getText(), createdComment.getText());
    }

    @Test
    void createComment_ShouldThrowBadRequestException_WhenNoBooking() {
        Long invalidUserId = 999L;
        CommentDto invalidCommentDto = new CommentDto(null, "Invalid Comment", null, "Invalid User", null);
        assertThrows(BadRequestException.class, () -> itemService
                .createComment(invalidCommentDto, invalidUserId, item.getId()));
    }
}