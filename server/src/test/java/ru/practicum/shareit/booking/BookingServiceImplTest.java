package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingCreate;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.validation.BookingValidation;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnAvaliableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingResolver resolver;

    @Mock
    private BookingValidation validation;

    @Mock
    private UserService userService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    private User user;
    private Booking booking;
    private BookingDto bookingDto;
    private BookingCreate bookingCreate;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User(1L, "User name", "user.email@test.com");

        item = new Item(1L, "Item Name", "Item Description", true, user.getId(), 1L);

        bookingCreate = new BookingCreate(item.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));

        booking = new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                item, user, BookingStatus.APPROVED);

        bookingDto = new BookingDto(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                item, user, BookingStatus.APPROVED);
    }

    @Test
    void createBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto createdBooking = bookingService.createBooking(bookingCreate, user.getId());

        assertNotNull(createdBooking);
        assertNotNull(createdBooking.getId());
        assertEquals(bookingCreate.getItemId(), createdBooking.getItem().getId());
        assertEquals(user.getId(), createdBooking.getBooker().getId());
    }

    @Test
    void acceptBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto acceptedBooking = bookingService.acceptBooking(user.getId(), booking.getId(), true);
        assertNotNull(acceptedBooking);
        assertEquals(BookingStatus.APPROVED, acceptedBooking.getStatus());
    }

    @Test
    void getBookingById() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDto foundBooking = bookingService.getBookingById(user.getId(), booking.getId());
        assertNotNull(foundBooking);
        assertEquals(booking.getId(), foundBooking.getId());
    }

    @Test
    void getBookings() {
        List<Booking> listBooking = List.of(booking);

        when(resolver
                .getBookingByState(BookingState.ALL, booking.getBooker().getId(), false))
                .thenReturn(listBooking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        List<BookingDto> bookings = bookingService.getBookings(user.getId(), "ALL");
        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        assertEquals(booking.getId(), bookings.getFirst().getId());
    }

    @Test
    void getBookingsByOwnerId() {
        List<Booking> listBooking = List.of(booking);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        when(resolver
                .getBookingByState(BookingState.ALL, booking.getBooker().getId(), true))
                .thenReturn(listBooking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        List<BookingDto> bookings = bookingService.getBookingByItemsOwnerId(user.getId(), "ALL");
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
        User anotherUser = new User(2L, "Another User", "another.user@test.com");

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        Long ownerId = anotherUser.getId();

        doThrow(ForbiddenException.class).when(validation).validateOwnerAccess(anyLong(), any(Booking.class));

        Long bookingId = booking.getId();
        assertThrows(ForbiddenException.class, () -> bookingService.acceptBooking(ownerId, bookingId, true));
    }

    @Test
    void createBooking_ShouldThrowUnAvaliableException_WhenItemNotAvailable() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Item unavailableItem = new Item(3L, "Unavailable Item", "Item Description",
                false, user.getId(), null);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(unavailableItem));
        doThrow(UnAvaliableException.class).when(validation).validateItemAvailable(any(Item.class));

        BookingCreate unavailableBookingCreate = new BookingCreate(unavailableItem.getId(),
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        assertThrows(UnAvaliableException.class, () -> bookingService
                .createBooking(unavailableBookingCreate, user.getId()));
    }
}