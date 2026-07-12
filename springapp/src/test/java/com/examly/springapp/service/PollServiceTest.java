package com.examly.springapp.service;

import com.examly.springapp.dto.PollCreateRequest;
import com.examly.springapp.model.Poll;
import com.examly.springapp.repository.PollRepository;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDateTime;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PollServiceTest {
    @Mock
    private PollRepository pollRepository;
    @Mock
    private Validator validator;

    @InjectMocks
    private PollService pollService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreatePollValidation() {
        PollCreateRequest req = new PollCreateRequest();
        req.setTitle("Abcd"); // too short
        req.setDescription("desc");
        req.setCreatedBy("user");
        req.setExpiresAt(LocalDateTime.now().plusDays(2));
        req.setPublic(true);
        req.setAllowAnonymous(false);
        PollCreateRequest.PollOptionRequest op1 = new PollCreateRequest.PollOptionRequest(); op1.setText("Java");
        req.setOptions(Collections.singletonList(op1)); // less than 2 options
        Exception ex = assertThrows(Exception.class, () -> pollService.createPoll(req));
        assertTrue(ex.getMessage().toLowerCase().contains("title") || ex.getMessage().toLowerCase().contains("option"));
    }

    @Test
    public void testCreatePollSuccessWithMocks() {
        PollCreateRequest req = new PollCreateRequest();
        req.setTitle("Favorite Language");
        req.setDescription("desc");
        req.setCreatedBy("user");
        req.setExpiresAt(LocalDateTime.now().plusDays(2));
        req.setPublic(true);
        req.setAllowAnonymous(false);
        PollCreateRequest.PollOptionRequest op1 = new PollCreateRequest.PollOptionRequest(); op1.setText("Java");
        PollCreateRequest.PollOptionRequest op2 = new PollCreateRequest.PollOptionRequest(); op2.setText("Python");
        req.setOptions(java.util.Arrays.asList(op1, op2));
        when(validator.validate(any(Poll.class))).thenReturn(java.util.Collections.emptySet());
        Poll poll = new Poll();
        poll.setId(1L);
        when(pollRepository.save(any(Poll.class))).thenReturn(poll);
        Poll result = pollService.createPoll(req);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }
}
