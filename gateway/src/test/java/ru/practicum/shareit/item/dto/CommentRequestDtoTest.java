package ru.practicum.shareit.item.dto;

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
class CommentRequestDtoTest {

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
        CommentRequestDto dto = new CommentRequestDto("This is a comment");

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"text\":\"This is a comment\"");
    }

    @Test
    void testDeserialization() throws Exception {
        String json = "{\"text\":\"This is a comment\"}";

        CommentRequestDto dto = objectMapper.readValue(json, CommentRequestDto.class);

        assertThat(dto.getText()).isEqualTo("This is a comment");
    }

    @Test
    void testValidation() {
        CommentRequestDto dto = new CommentRequestDto("This is a comment");

        Set<ConstraintViolation<CommentRequestDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void testValidationFailureText() {
        CommentRequestDto dto = new CommentRequestDto("");

        Set<ConstraintViolation<CommentRequestDto>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(violation -> violation.getPropertyPath()
                .toString().equals("text"));
    }
}