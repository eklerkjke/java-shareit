package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingCreate;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.validation.BookingValidation;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingValidation validation;

    @Override
    @Transactional
    public BookingDto createBooking(BookingCreate bookingCreate, Long userId) {
        log.info("Создание нового резервирования: {}, пользователем с id: {}", bookingCreate, userId);

        validation.validateBookingDates(bookingCreate);

        User booker = getUserById(userId);
        Item item = getItemById(bookingCreate.getItemId());

        validation.validateItemAvailable(item);

        Booking booking = bookingRepository.save(BookingMapper.mapToBooking(bookingCreate, item, booker));
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto acceptBooking(Long ownerId, Long bookingId, Boolean approved) {
        log.info("Подтверждение резервирования по ID: {}", bookingId);
        Booking booking = findById(bookingId);
        validation.validateOwnerAccess(ownerId, booking);

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        log.info("Получение данных о резервировании по ID: {}", bookingId);
        Booking booking = findById(bookingId);
        validation.validateUserAccess(userId, booking);

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookings(Long userId, String status) {
        log.info("Получение данных о резервировании по ID пользователя: {}", userId);

        getUserById(userId);
        BookingStatus state = parseBookingStatus(status);

        return status == null ?
                BookingMapper.mapToBookingDto(bookingRepository.findAllByBookerId(userId)) :
                BookingMapper.mapToBookingDto(bookingRepository.findAllByBookerIdAndStatus(userId, state));
    }

    @Override
    public List<BookingDto> getBookingsByOwnerId(Long ownerId, String status) {
        log.info("Получение данных о резервировании по предметам у пользователя с ID: {}", ownerId);
        getUserById(ownerId);

        BookingStatus state = parseBookingStatus(status);

        return status == null ?
                BookingMapper.mapToBookingDto(bookingRepository.findAllByItemOwnerId(ownerId)) :
                BookingMapper.mapToBookingDto(bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, state));
    }

    private Booking findById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найден резервирование с id: " + id));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id: " + userId));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найден предмет с id: " + itemId));
    }

    private BookingStatus parseBookingStatus(String status) {
        try {
            return BookingStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Не известный статус: " + status);
        }
    }
}