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
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnAvaliableException;
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

    @Override
    @Transactional
    public BookingDto createBooking(BookingCreate bookingCreate, Long userId) {
        log.info("Создание нового резервирования: {}, пользователем с id: {}", bookingCreate, userId);

        validateBookingDates(bookingCreate);

        User booker = getUserById(userId);
        Item item = getItemById(bookingCreate.getItemId());

        validateItemAvailable(item);

        Booking booking = bookingRepository.save(BookingMapper.mapToBooking(bookingCreate, item, booker));
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto acceptBooking(Long ownerId, Long bookingId, Boolean approved) {
        log.info("Подтверждение резервирования по ID: {}", bookingId);
        Booking booking = findById(bookingId);
        validateOwnerAccess(ownerId, booking);

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        log.info("Получение данных о резервировании по ID: {}", bookingId);
        Booking booking = findById(bookingId);
        validateUserAccess(userId, booking);

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookings(Long userId, BookingStatus status) {
        log.info("Получение данных о резервировании по ID пользователя: {}", userId);

        getUserById(userId);

        return status == null ?
                BookingMapper.mapToBookingDto(bookingRepository.findAllByBookerId(userId)) :
                BookingMapper.mapToBookingDto(bookingRepository.findAllByBookerIdAndStatus(userId, status));
    }

    @Override
    public List<BookingDto> getBookingsByOwnerId(Long ownerId, BookingStatus status) {
        log.info("Получение данных о резервировании по предметам у пользователя с ID: {}", ownerId);
        getUserById(ownerId);

        return status == null ?
                BookingMapper.mapToBookingDto(bookingRepository.findAllByItemOwnerId(ownerId)) :
                BookingMapper.mapToBookingDto(bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, status));
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

    private void validateBookingDates(BookingCreate bookingCreate) {
        if (bookingCreate.getStart().equals(bookingCreate.getEnd())) {
            throw new BadRequestException("Дата начала бронирования не должна совпадать " +
                    "с датой окончания бронирования");
        }
        if (bookingCreate.getStart().isAfter(bookingCreate.getEnd())) {
            throw new BadRequestException("Дата начала бронирования не должна быть позже " +
                    "чем дата окончания бронирования");
        }
    }

    private void validateItemAvailable(Item item) {
        if (!item.getAvailable()) {
            throw new UnAvaliableException("Для бронирования не доступен предмет с id: " + item.getId());
        }
    }

    private void validateOwnerAccess(Long ownerId, Booking booking) {
        if (!ownerId.equals(booking.getItem().getOwnerId())) {
            throw new ForbiddenException("Изменять статус может только владелец предмета");
        }
    }

    private void validateUserAccess(Long userId, Booking booking) {
        if (!(userId.equals(booking.getBooker().getId()) || userId.equals(booking.getItem().getOwnerId()))) {
            throw new ForbiddenException("Просмотреть резервирование может только владелец предмета или " +
                    "пользователь сделавший запрос на резервирование");
        }
    }
}