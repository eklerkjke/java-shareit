package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b " +
            "join b.item it " +
            "where it.ownerId = ?1")
    List<Booking> findAllByItemOwnerId(Long ownerId);

    @Query ("select b from Booking b " +
             "where booker.id = ?1 " +
             "order by b.start desc")
    List<Booking> findAllBookings(Long ownerId);

    @Query("select b from Booking b " +
            "where booker.id = ?1 " +
            "and (b.start <?2 and b.end >?2)" +
            "order by b.start desc")
    List<Booking> findCurrentBookingsByOwnerId(Long ownerId, LocalDateTime currentDate);

    @Query("select b from Booking b " +
            "join b.item it " +
            "where it.ownerId = ?1" +
            "and (b.start <?2 and b.end >?2)" +
            "order by b.start desc")
    List<Booking> findCurrentBookingsByItemsOwnerId(Long ownerId, LocalDateTime currentDate);

    @Query("select b from Booking b " +
            "where booker.id = ?1 " +
            "and b.end <?2" +
            "order by b.start desc")
    List<Booking> findPastBookingsByOwnerId(Long ownerId, LocalDateTime currentDate);

    @Query("select b from Booking b " +
            "join b.item it " +
            "where it.ownerId = ?1" +
            "and b.end <?2" +
            "order by b.start desc")
    List<Booking> findPastBookingsByItemsOwnerId(Long ownerId, LocalDateTime currentDate);

    @Query("select b from Booking b " +
            "where booker.id = ?1 " +
            "and b.start >?2" +
            "order by b.start desc")
    List<Booking> findFutureBookingsByOwnerId(Long ownerId, LocalDateTime currentDate);

    @Query("select b from Booking b " +
            "join b.item it " +
            "where it.ownerId = ?1" +
            "and b.start >?2" +
            "order by b.start desc")
    List<Booking> findFutureBookingsByItemsOwnerId(Long ownerId, LocalDateTime currentDate);

    @Query("select b from Booking b " +
            "join b.item it " +
            "where it.ownerId = ?1 and b.status = ?2")
    List<Booking> findAllByBookingsItemsOwnerIdAndStatus(Long ownerId, BookingStatus status);

    @Query("select b from Booking b " +
            "where booker.id = ?1 and b.status = ?2")
    List<Booking> findAllByBookingsOwnerIdAndStatus(Long ownerId, BookingStatus status);

    @Query("select b from Booking b " +
            "join b.item it " +
            "where it.ownerId = ?1 and b.start < CURRENT_TIMESTAMP " +
            "order by b.start desc")
    Booking findLastBooking(Long itemId);

    @Query("select b from Booking b " +
            "join b.item it " +
            "where it.ownerId = ?1 and b.start > CURRENT_TIMESTAMP " +
            "order by b.start asc")
    Booking findNextBooking(Long itemId);

    @Query("select count(b) from Booking b " +
            "where b.item.id = ?1 and b.booker.id = ?2 and b.end < CURRENT_TIMESTAMP and b.status = 'APPROVED'")
    Integer checkHaveBooking(Long itemId, Long userId);

    @Query("select b from Booking b " +
            "join b.item it " +
            "where it.ownerId in ?1 and b.start < CURRENT_TIMESTAMP " +
            "order by b.start desc")
    List<Booking> findLastBookings(List<Long> itemIds);

    @Query("select b from Booking b " +
            "join b.item it " +
            "where it.ownerId in ?1 and b.start > CURRENT_TIMESTAMP " +
            "order by b.start asc")
    List<Booking> findNextBookings(List<Long> itemIds);
}