package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import java.util.List;

public interface BookingResolver {
    List<Booking> getBookingByState(BookingState state, Long ownerId, boolean isOwner);
}
