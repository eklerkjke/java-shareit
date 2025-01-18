package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingResolverImplTest {
    @InjectMocks
    private BookingResolverImpl resolver;

    @Mock
    private BookingRepository repository;

    private Booking bookingApproved;
    private Booking bookingRejected;
    private Booking bookingWithItems;
    private Booking bookingWaiting;
    private Booking bookingRejectedTwo;
    private Item item;

    @BeforeEach
    void setUp() {
        item = new Item(1L, "Item Name", "Item Description", true, 1L, 1L);

        bookingApproved = new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                null, null, BookingStatus.APPROVED);
        bookingRejected = new Booking(2L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                null, null, BookingStatus.REJECTED);
        bookingWithItems = new Booking(3L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                item, null, BookingStatus.WAITING);
        bookingWaiting = new Booking(4L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusHours(1),
                null, null, BookingStatus.WAITING);
        bookingRejectedTwo = new Booking(5L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                null, null, BookingStatus.REJECTED);
    }

    @Test
    void getBookingsByOwner() {
        List<Booking> list = List.of(bookingApproved, bookingRejected, bookingWithItems);
        when(repository.findAllByItemOwnerId(anyLong())).thenReturn(list);

        List<Booking> bookingList = resolver.getBookingByState(BookingState.ALL, anyLong(), true);
        assertFalse(bookingList.isEmpty());
        assertEquals(bookingList.getFirst().getId(), bookingApproved.getId());
        assertEquals(bookingList.getLast().getId(), bookingWithItems.getId());
    }

    @Test
    void getBookingByNotOwner() {
        List<Booking> list = List.of(bookingWithItems);
        when(repository.findAllByBookingsOwnerIdAndStatus(anyLong(), any())).thenReturn(list);

        List<Booking> bookingList = resolver.getBookingByState(BookingState.WAITING, 1L, false);
        assertFalse(bookingList.isEmpty());
        assertEquals(bookingList.getFirst().getId(), bookingWithItems.getId());
        assertEquals(bookingList.getFirst().getStatus(), bookingWithItems.getStatus());
    }

    @Test
    void getEmptyListBookings() {
        List<Booking> list = List.of();
        when(repository.findPastBookingsByItemsOwnerId(anyLong(), any())).thenReturn(list);

        List<Booking> bookingList = resolver.getBookingByState(BookingState.PAST, 1L, true);
        assertTrue(bookingList.isEmpty());
    }

    @Test
    void testGetBookingByStateAll() {
        when(repository.findAllByItemOwnerId(anyLong())).thenReturn(List.of(bookingWaiting, bookingRejectedTwo));

        List<Booking> bookings = resolver.getBookingByState(BookingState.ALL, 1L, true);

        assertNotNull(bookings);
        assertEquals(2, bookings.size());
    }

    @Test
    void testGetBookingByStateCurrent() {
        when(repository.findCurrentBookingsByItemsOwnerId(anyLong(), any(LocalDateTime.class))).thenReturn(List.of(bookingWithItems));

        List<Booking> bookings = resolver.getBookingByState(BookingState.CURRENT, 1L, true);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    void testGetBookingByStatePast() {
        when(repository.findPastBookingsByItemsOwnerId(anyLong(), any(LocalDateTime.class))).thenReturn(List.of(bookingRejectedTwo));

        List<Booking> bookings = resolver.getBookingByState(BookingState.PAST, 1L, true);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    void testGetBookingByStateFuture() {
        when(repository.findFutureBookingsByItemsOwnerId(anyLong(), any(LocalDateTime.class))).thenReturn(List.of(bookingRejectedTwo));

        List<Booking> bookings = resolver.getBookingByState(BookingState.FUTURE, 1L, true);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    void testGetBookingByStateWaiting() {
        when(repository.findAllByBookingsItemsOwnerIdAndStatus(anyLong(), any(BookingStatus.class))).thenReturn(List.of(bookingWaiting));

        List<Booking> bookings = resolver.getBookingByState(BookingState.WAITING, 1L, true);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    void testGetBookingByStateRejected() {
        when(repository.findAllByBookingsItemsOwnerIdAndStatus(anyLong(), any(BookingStatus.class))).thenReturn(List.of(bookingRejected));

        List<Booking> bookings = resolver.getBookingByState(BookingState.REJECTED, 1L, true);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }


    @Test
    void testGetBookingByStateAllNotOwner() {
        when(repository.findAllBookings(anyLong())).thenReturn(List.of(bookingWaiting, bookingRejectedTwo));

        List<Booking> bookings = resolver.getBookingByState(BookingState.ALL, 1L, false);

        assertNotNull(bookings);
        assertEquals(2, bookings.size());
    }

    @Test
    void testGetBookingByStateCurrentNotOwner() {
        when(repository.findCurrentBookingsByOwnerId(anyLong(), any(LocalDateTime.class))).thenReturn(List.of(bookingWithItems));

        List<Booking> bookings = resolver.getBookingByState(BookingState.CURRENT, 1L, false);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    void testGetBookingByStatePastNotOwner() {
        when(repository.findPastBookingsByOwnerId(anyLong(), any(LocalDateTime.class))).thenReturn(List.of(bookingRejectedTwo));

        List<Booking> bookings = resolver.getBookingByState(BookingState.PAST, 1L, false);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    void testGetBookingByStateFutureNotOwner() {
        when(repository.findFutureBookingsByOwnerId(anyLong(), any(LocalDateTime.class))).thenReturn(List.of(bookingRejectedTwo));

        List<Booking> bookings = resolver.getBookingByState(BookingState.FUTURE, 1L, false);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    void testGetBookingByStateWaitingNotOwner() {
        when(repository.findAllByBookingsOwnerIdAndStatus(anyLong(), any(BookingStatus.class))).thenReturn(List.of(bookingWaiting));

        List<Booking> bookings = resolver.getBookingByState(BookingState.WAITING, 1L, false);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    void testGetBookingByStateRejectedNotOwner() {
        when(repository.findAllByBookingsOwnerIdAndStatus(anyLong(), any(BookingStatus.class))).thenReturn(List.of(bookingRejected));

        List<Booking> bookings = resolver.getBookingByState(BookingState.REJECTED, 1L, false);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }
}