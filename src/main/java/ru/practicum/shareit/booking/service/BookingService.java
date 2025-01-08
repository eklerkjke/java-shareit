package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreate;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingCreate booking, Long userId);

    BookingDto acceptBooking(Long ownerId, Long bookingId, Boolean approved);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getBookings(Long userId, String status);

    List<BookingDto> getBookingsByOwnerId(Long ownerId, String status);
}