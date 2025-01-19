package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingCreate {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}