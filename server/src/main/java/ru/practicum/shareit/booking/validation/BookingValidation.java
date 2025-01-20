package ru.practicum.shareit.booking.validation;

import ru.practicum.shareit.booking.dto.BookingCreate;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

public interface BookingValidation {

    void validateBookingDates(BookingCreate bookingCreate);

    void validateItemAvailable(Item item);

    void validateOwnerAccess(Long ownerId, Booking booking);

    void validateUserAccess(Long userId, Booking booking);
}
