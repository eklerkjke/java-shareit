package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.validation.CreateValidationGroup;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@JsonTest
class UserRequestDtoTest {

    @Autowired
    private ObjectMapper objectMapper;

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testSerialization() throws Exception {
        UserRequestDto dto = new UserRequestDto("User name", "user.email@test.com");

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"name\":\"User name\"");
        assertThat(json).contains("\"email\":\"user.email@test.com\"");
    }

    @Test
    void testDeserialization() throws Exception {
        String json = "{\"name\":\"User name\",\"email\":\"user.email@test.com\"}";

        UserRequestDto dto = objectMapper.readValue(json, UserRequestDto.class);

        assertThat(dto.getName()).isEqualTo("User name");
        assertThat(dto.getEmail()).isEqualTo("user.email@test.com");
    }

    @Test
    void testValidation() {
        UserRequestDto dto = new UserRequestDto("User name", "user.email@test.com");

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto, CreateValidationGroup.class);
        assertThat(violations).isEmpty();
    }

    @Test
    void testValidationFailureName() {
        UserRequestDto dto = new UserRequestDto("", "user.email@test.com");

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto, CreateValidationGroup.class);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(violation -> violation.getPropertyPath()
                .toString().equals("name"));
    }

    @Test
    void testValidationFailureEmail() {
        UserRequestDto dto = new UserRequestDto("User name", "invalid-email");

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto, CreateValidationGroup.class);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(violation -> violation.getPropertyPath()
                .toString().equals("email"));
    }
}