package ru.practicum.shareit.booking.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingCreate;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.UnAvaliableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookingValidationImplTest {
    @InjectMocks
    private BookingValidationImpl validation;

    @Test
    void validateBookingDates() {
        BookingCreate bookingCreate = new BookingCreate(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        assertDoesNotThrow(() -> validation.validateBookingDates(bookingCreate));
    }

    @Test
    void validateItemAvailable() {
        Item item = new Item(1L, "Test name", "Test descr", true, 1L, 1L);
        assertDoesNotThrow(() -> validation.validateItemAvailable(item));
    }

    @Test
    void validateUnAvaliableExceptionItemAvailable() {
        Item item = new Item(1L, "Test name", "Test descr", false, 1L, 1L);
        assertThrows(UnAvaliableException.class, () -> validation.validateItemAvailable(item));
    }

    @Test
    void validateOwnerAccess() {
        Item item = new Item(1L, "Test name", "Test descr", true, 1L, 1L);

        Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, null, BookingStatus.APPROVED);

        assertDoesNotThrow(() -> validation.validateOwnerAccess(1L, booking));
    }

    @Test
    void validateForbiddenExceptionOwnerAccess() {
        Item item = new Item(1L, "Test name", "Test descr", true, 1L, 1L);
        Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, null, BookingStatus.APPROVED);

        assertThrows(ForbiddenException.class, () -> validation.validateOwnerAccess(2L, booking));
    }

    @Test
    void validateUserAccess() {
        User user = new User(1L, "User name", "user.email@test.com");
        Item item = new Item(1L, "Test name", "Test descr", true, 1L, 1L);
        Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, user, BookingStatus.APPROVED);

        assertDoesNotThrow(() -> validation.validateUserAccess(user.getId(), booking));
    }

    @Test
    void validateForbiddenExceptionUserAccess() {
        User user = new User(1L, "User name", "user.email@test.com");
        Item item = new Item(1L, "Test name", "Test descr", true, 1L, 1L);
        Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, user, BookingStatus.APPROVED);

        assertThrows(ForbiddenException.class, () -> validation.validateUserAccess(2L, booking));
    }
}