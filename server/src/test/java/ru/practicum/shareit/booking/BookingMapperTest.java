package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.dto.BookingCreate;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class BookingMapperTest {

    @Test
    void toBookingDto() {
        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                null, null, BookingStatus.APPROVED);

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getItem(), bookingDto.getItem());
    }

    @Test
    void mapToBooking() {
        User user = new User(1L, "User name", "user.email@test.com");
        Item item = new Item(1L, "Item Name", "Item Description", true, user.getId(), 1L);

        BookingCreate bookingCreate = new BookingCreate(item.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));

        Booking booking = BookingMapper.mapToBooking(bookingCreate, item, user);

        assertEquals(booking.getStatus(), BookingStatus.WAITING);
        assertEquals(booking.getItem().getId(), item.getId());
    }
}