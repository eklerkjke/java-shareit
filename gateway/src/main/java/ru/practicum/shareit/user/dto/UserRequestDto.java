package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.CreateValidationGroup;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

    @NotBlank(groups = CreateValidationGroup.class)
    private String name;

    @NotBlank(groups = CreateValidationGroup.class)
    @Email(groups = CreateValidationGroup.class)
    private String email;
}