package com.examly.springapp.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class PollTest {
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testPollModelValidation() {
        Poll poll = new Poll();
        poll.setTitle("");
        poll.setDescription("This is a test description");
        poll.setCreatedBy("");
        poll.setCreatedAt(LocalDateTime.now());
        poll.setExpiresAt(LocalDateTime.now().minusDays(1)); // invalid, past
        poll.setPublic(true);
        poll.setAllowAnonymous(false);
        Set<PollOption> opts = new HashSet<>();
        poll.setOptions(new java.util.ArrayList<>());

        Set<ConstraintViolation<Poll>> violations = validator.validate(poll);
        assertFalse(violations.isEmpty());
        boolean foundTitle = violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title"));
        boolean foundExpiresAt = violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("expiresAt"));
        assertTrue(foundTitle);
        assertTrue(foundExpiresAt);
    }
}
