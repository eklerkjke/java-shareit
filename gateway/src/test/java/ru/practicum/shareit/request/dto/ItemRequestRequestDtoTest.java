package ru.practicum.shareit.request.dto;

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

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@JsonTest
class ItemRequestRequestDtoTest {

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
        ItemRequestRequestDto dto = new ItemRequestRequestDto("This is a description");

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"description\":\"This is a description\"");
    }

    @Test
    void testDeserialization() throws Exception {
        String json = "{\"description\":\"This is a description\"}";

        ItemRequestRequestDto dto = objectMapper.readValue(json, ItemRequestRequestDto.class);

        assertThat(dto.getDescription()).isEqualTo("This is a description");
    }

    @Test
    void testValidation() {
        ItemRequestRequestDto dto = new ItemRequestRequestDto("This is a description");

        Set<ConstraintViolation<ItemRequestRequestDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void testValidationFailureDescription() {
        ItemRequestRequestDto dto = new ItemRequestRequestDto("");

        Set<ConstraintViolation<ItemRequestRequestDto>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(violation -> violation.getPropertyPath()
                .toString().equals("description"));
    }
}