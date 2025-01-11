package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class BookingResolver {
    private final BookingRepository repository;

    public List<Booking> getBookingByState(BookingState state, Long ownerId, boolean isOwner) {
        if (isOwner) {
            return switch (state) {
                case ALL -> repository.findAllByItemOwnerId(ownerId);
                case CURRENT -> repository.findCurrentBookingsByItemsOwnerId(ownerId, LocalDateTime.now());
                case PAST -> repository.findPastBookingsByItemsOwnerId(ownerId, LocalDateTime.now());
                case FUTURE -> repository.findFutureBookingsByItemsOwnerId(ownerId, LocalDateTime.now());
                case WAITING -> repository.findAllByBookingsItemsOwnerIdAndStatus(ownerId, BookingStatus.WAITING);
                case REJECTED -> repository.findAllByBookingsItemsOwnerIdAndStatus(ownerId, BookingStatus.REJECTED);
            };
        } else {
            return switch (state) {
                case ALL -> repository.findAllBookings(ownerId);
                case CURRENT -> repository.findCurrentBookingsByOwnerId(ownerId, LocalDateTime.now());
                case PAST -> repository.findPastBookingsByOwnerId(ownerId, LocalDateTime.now());
                case FUTURE -> repository.findFutureBookingsByOwnerId(ownerId, LocalDateTime.now());
                case WAITING -> repository.findAllByBookingsOwnerIdAndStatus(ownerId, BookingStatus.WAITING);
                case REJECTED -> repository.findAllByBookingsOwnerIdAndStatus(ownerId, BookingStatus.REJECTED);
            };
        }
    }
}
