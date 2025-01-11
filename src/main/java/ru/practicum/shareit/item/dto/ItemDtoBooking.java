package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;


@Data
public class ItemDtoBooking {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
    private Long requestId;
    private Booking lastBooking;
    private Booking nextBooking;
    private List<CommentDto> comments;
}