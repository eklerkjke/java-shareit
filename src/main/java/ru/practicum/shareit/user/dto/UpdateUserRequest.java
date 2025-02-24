package ru.practicum.shareit.user.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
}