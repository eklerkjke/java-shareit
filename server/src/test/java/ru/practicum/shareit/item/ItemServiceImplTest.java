package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class ItemServiceImplTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User user;
    private Item item;
    private ItemDto itemDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        // Создание и сохранение пользователя
        user = new User(null, "User name", "user.email@test.com");
        user = userRepository.save(user);

        // Создание и сохранение предмета
        item = new Item(null, "Item Name", "Item Description", true, user.getId(), null);
        item = itemRepository.save(item);

        // Создание DTO для тестов
        itemDto = new ItemDto(null, "Item Name", "Item Description", true, user.getId(), null, null);
        commentDto = new CommentDto(null, "Comment text", null, user.getName(), null);

        // Создание бронирования для тестов
        Booking booking = new Booking(null, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                item, user, BookingStatus.APPROVED);
        booking = bookingRepository.save(booking);
    }

    @Test
    void createItem() {
        ItemDto createdItem = itemService.createItem(itemDto);
        assertNotNull(createdItem);
        assertNotNull(createdItem.getId());
        assertEquals(itemDto.getName(), createdItem.getName());
        assertEquals(itemDto.getDescription(), createdItem.getDescription());
    }

    @Test
    void getAllItemByOwnerId() {
        List<ItemDtoBooking> items = itemService.getAllItemByOwnerId(user.getId());
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(item.getName(), items.getFirst().getName());
    }

    @Test
    void getItemById() {
        ItemDtoBooking foundItem = itemService.getItemById(item.getId());
        assertNotNull(foundItem);
        assertEquals(item.getName(), foundItem.getName());
    }

    @Test
    void updateItem() {
        itemDto.setId(item.getId());
        itemDto.setName("Updated Item Name");
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
        List<ItemDto> items = itemService.searchItemsByText("Item Name");
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(item.getName(), items.getFirst().getName());
    }

    @Test
    void createComment() {
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