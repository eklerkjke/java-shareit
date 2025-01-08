package ru.practicum.shareit.booking.validation;

import ru.practicum.shareit.booking.dto.BookingCreate;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.UnAvaliableException;
import ru.practicum.shareit.item.model.Item;

public class BookingValidationImpl {

    public void validateBookingDates(BookingCreate bookingCreate) {
        if (bookingCreate.getStart().equals(bookingCreate.getEnd())) {
            throw new BadRequestException("Дата начала бронирования не должна совпадать " +
                    "с датой окончания бронирования");
        }
        if (bookingCreate.getStart().isAfter(bookingCreate.getEnd())) {
            throw new BadRequestException("Дата начала бронирования не должна быть позже " +
                    "чем дата окончания бронирования");
        }
    }

    public void validateItemAvailable(Item item) {
        if (!item.getAvailable()) {
            throw new UnAvaliableException("Для бронирования не доступен предмет с id: " + item.getId());
        }
    }

    public void validateOwnerAccess(Long ownerId, Booking booking) {
        if (!ownerId.equals(booking.getItem().getOwnerId())) {
            throw new ForbiddenException("Изменять статус может только владелец предмета");
        }
    }

    public void validateUserAccess(Long userId, Booking booking) {
        if (!(userId.equals(booking.getBooker().getId()) || userId.equals(booking.getItem().getOwnerId()))) {
            throw new ForbiddenException("Просмотреть резервирование может только владелец предмета или " +
                    "пользователь сделавший запрос на резервирование");
        }
    }
}
