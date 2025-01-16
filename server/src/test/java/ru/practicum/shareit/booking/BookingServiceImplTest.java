package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreate;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnAvaliableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class BookingServiceImplTest {

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User user;
    private Booking booking;
    private BookingCreate bookingCreate;

    @BeforeEach
    void setUp() {
        // Создание и сохранение пользователя
        user = new User(null, "User name", "user.email@test.com");
        user = userRepository.save(user);

        // Создание и сохранение предмета
        Item item = new Item(null, "Item Name", "Item Description", true, user.getId(), null);
        item = itemRepository.save(item);

        // Создание DTO для тестов
        bookingCreate = new BookingCreate(item.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));

        // Создание бронирования для тестов
        booking = new Booking(null, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                item, user, BookingStatus.APPROVED);
        booking = bookingRepository.save(booking);
    }

    @Test
    void createBooking() {
        BookingDto createdBooking = bookingService.createBooking(bookingCreate, user.getId());
        assertNotNull(createdBooking);
        assertNotNull(createdBooking.getId());
        assertEquals(bookingCreate.getItemId(), createdBooking.getItem().getId());
        assertEquals(user.getId(), createdBooking.getBooker().getId());
    }

    @Test
    void acceptBooking() {
        BookingDto acceptedBooking = bookingService.acceptBooking(user.getId(), booking.getId(), true);
        assertNotNull(acceptedBooking);
        assertEquals(BookingStatus.APPROVED, acceptedBooking.getStatus());
    }

    @Test
    void getBookingById() {
        BookingDto foundBooking = bookingService.getBookingById(user.getId(), booking.getId());
        assertNotNull(foundBooking);
        assertEquals(booking.getId(), foundBooking.getId());
    }

    @Test
    void getBookings() {
        List<BookingDto> bookings = bookingService.getBookings(user.getId(), null);
        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(booking.getId(), bookings.getFirst().getId());
    }

    @Test
    void getBookingsByOwnerId() {
        List<BookingDto> bookings = bookingService.getBookingByItemsOwnerId(user.getId(), null);
        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(booking.getId(), bookings.getFirst().getId());
    }

    @Test
    void getBookingByIdNotFound() {
        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(user.getId(), 999L));
    }

    @Test
    void acceptBooking_ShouldThrowForbiddenException_WhenNotOwner() {
        User anotherUser = new User(null, "Another User", "another.user@test.com");
        anotherUser = userRepository.save(anotherUser);
        Long ownerId = anotherUser.getId();
        Long bookingId = booking.getId();
        assertThrows(ForbiddenException.class, () -> bookingService.acceptBooking(ownerId, bookingId, true));
    }

    @Test
    void createBooking_ShouldThrowUnAvaliableException_WhenItemNotAvailable() {
        Item unavailableItem = new Item(null, "Unavailable Item", "Item Description",
                false, user.getId(), null);
        unavailableItem = itemRepository.save(unavailableItem);
        BookingCreate unavailableBookingCreate = new BookingCreate(unavailableItem.getId(),
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        assertThrows(UnAvaliableException.class, () -> bookingService
                .createBooking(unavailableBookingCreate, user.getId()));
    }
}