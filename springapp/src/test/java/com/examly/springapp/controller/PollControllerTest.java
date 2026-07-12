package com.examly.springapp.controller;

import com.examly.springapp.dto.PollCreateRequest;
import com.examly.springapp.dto.PollVoteRequest;
import com.examly.springapp.model.Poll;
import com.examly.springapp.model.PollOption;
import com.examly.springapp.repository.PollRepository;
import com.examly.springapp.repository.PollOptionRepository;
import com.examly.springapp.repository.VoteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
public class PollControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PollRepository pollRepository;
    @Autowired
    private PollOptionRepository pollOptionRepository;

    @BeforeEach
    public void setup() {
        pollOptionRepository.deleteAll();
        pollRepository.deleteAll();
    }

    @Test
    public void testCreatePollSuccess() throws Exception {
        PollCreateRequest req = new PollCreateRequest();
        req.setTitle("Favorite Language");
        req.setDescription("desc");
        req.setCreatedBy("user1");
        req.setExpiresAt(LocalDateTime.now().plusDays(2));
        req.setPublic(true);
        req.setAllowAnonymous(false);
        PollCreateRequest.PollOptionRequest op1 = new PollCreateRequest.PollOptionRequest(); op1.setText("Java");
        PollCreateRequest.PollOptionRequest op2 = new PollCreateRequest.PollOptionRequest(); op2.setText("Python");
        req.setOptions(Arrays.asList(op1, op2));
        mockMvc.perform(post("/api/polls")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Favorite Language"));
    }

    @Test
    public void testCreatePollValidationFailure() throws Exception {
        PollCreateRequest req = new PollCreateRequest();
        req.setTitle("");
        req.setDescription("desc");
        req.setCreatedBy("");
        req.setExpiresAt(LocalDateTime.now().minusDays(1));
        req.setPublic(true);
        req.setAllowAnonymous(false);
        PollCreateRequest.PollOptionRequest op1 = new PollCreateRequest.PollOptionRequest(); op1.setText("");
        req.setOptions(Arrays.asList(op1));
        mockMvc.perform(post("/api/polls")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", not(emptyString())));
    }

    @Test
    public void testGetPublicPolls() throws Exception {
        PollCreateRequest req = new PollCreateRequest();
        req.setTitle("Favorite Language");
        req.setDescription("desc");
        req.setCreatedBy("user1");
        req.setExpiresAt(LocalDateTime.now().plusDays(2));
        req.setPublic(true);
        req.setAllowAnonymous(false);
        PollCreateRequest.PollOptionRequest op1 = new PollCreateRequest.PollOptionRequest(); op1.setText("Java");
        PollCreateRequest.PollOptionRequest op2 = new PollCreateRequest.PollOptionRequest(); op2.setText("Python");
        req.setOptions(Arrays.asList(op1, op2));
        mockMvc.perform(post("/api/polls")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/polls/public?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.content[0].title").value("Favorite Language"));
    }

    @Test
    public void testGetPollDetails() throws Exception {
        PollCreateRequest req = new PollCreateRequest();
        req.setTitle("Test Poll");
        req.setDescription("desc");
        req.setCreatedBy("tester");
        req.setExpiresAt(LocalDateTime.now().plusDays(1));
        req.setPublic(false);
        req.setAllowAnonymous(true);
        PollCreateRequest.PollOptionRequest op1 = new PollCreateRequest.PollOptionRequest(); op1.setText("A");
        PollCreateRequest.PollOptionRequest op2 = new PollCreateRequest.PollOptionRequest(); op2.setText("B");
        req.setOptions(Arrays.asList(op1, op2));
        String json = objectMapper.writeValueAsString(req);
        String res = mockMvc.perform(post("/api/polls").contentType(MediaType.APPLICATION_JSON).content(json)).andReturn().getResponse().getContentAsString();
        Map<String, Object> pollMap = objectMapper.readValue(res, HashMap.class);
        Long pollId = Long.valueOf(((Number) pollMap.get("id")).longValue());
        mockMvc.perform(get("/api/polls/" + pollId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Poll"));
        mockMvc.perform(get("/api/polls/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCastVoteSuccessAndPreventDuplicateVoting() throws Exception {
        PollCreateRequest req = new PollCreateRequest();
        req.setTitle("VotePoll");
        req.setDescription("desc");
        req.setCreatedBy("voteuser");
        req.setExpiresAt(LocalDateTime.now().plusDays(2));
        req.setPublic(true);
        req.setAllowAnonymous(false);
        PollCreateRequest.PollOptionRequest op1 = new PollCreateRequest.PollOptionRequest(); op1.setText("Java");
        PollCreateRequest.PollOptionRequest op2 = new PollCreateRequest.PollOptionRequest(); op2.setText("Python");
        req.setOptions(Arrays.asList(op1, op2));
        String json = objectMapper.writeValueAsString(req);
        String res = mockMvc.perform(post("/api/polls").contentType(MediaType.APPLICATION_JSON).content(json)).andReturn().getResponse().getContentAsString();
        Map<String, Object> pollMap = objectMapper.readValue(res, HashMap.class);
        Long pollId = Long.valueOf(((Number) pollMap.get("id")).longValue());
        // Get options
        Poll poll = pollRepository.findById(pollId).get();
        Long optionId = poll.getOptions().get(0).getId();
        // Success vote
        PollVoteRequest vreq = new PollVoteRequest();
        vreq.setOptionId(optionId);
        vreq.setUsername("voter1");
        mockMvc.perform(post("/api/polls/" + pollId + "/vote")
           .contentType(MediaType.APPLICATION_JSON)
           .content(objectMapper.writeValueAsString(vreq)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.message", containsString("Vote cast successfully")));
        // Duplicate
        mockMvc.perform(post("/api/polls/" + pollId + "/vote")
           .contentType(MediaType.APPLICATION_JSON)
           .content(objectMapper.writeValueAsString(vreq)))
           .andExpect(status().isForbidden());
    }

    @Test
    public void testGetPollResults() throws Exception {
        PollCreateRequest req = new PollCreateRequest();
        req.setTitle("Result Poll");
        req.setDescription("desc");
        req.setCreatedBy("userres");
        req.setExpiresAt(LocalDateTime.now().plusDays(1));
        req.setPublic(true);
        req.setAllowAnonymous(false);
        PollCreateRequest.PollOptionRequest op1 = new PollCreateRequest.PollOptionRequest(); op1.setText("Java");
        PollCreateRequest.PollOptionRequest op2 = new PollCreateRequest.PollOptionRequest(); op2.setText("Python");
        req.setOptions(Arrays.asList(op1, op2));
        String json = objectMapper.writeValueAsString(req);
        String res = mockMvc.perform(post("/api/polls").contentType(MediaType.APPLICATION_JSON).content(json)).andReturn().getResponse().getContentAsString();
        Map<String, Object> pollMap = objectMapper.readValue(res, HashMap.class);
        Long pollId = Long.valueOf(((Number) pollMap.get("id")).longValue());
        Poll poll = pollRepository.findById(pollId).get();
        Long optionId = poll.getOptions().get(0).getId();
        // Cast a vote
        PollVoteRequest vreq = new PollVoteRequest();
        vreq.setOptionId(optionId);
        vreq.setUsername("voter1");
        mockMvc.perform(post("/api/polls/" + pollId + "/vote")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(vreq)))
        .andExpect(status().isOk());
                // Now get results
                mockMvc.perform(get("/api/polls/" + pollId + "/results"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(pollId))
                        .andExpect(jsonPath("$.totalVotes").value(greaterThanOrEqualTo(1)))
                        .andExpect(jsonPath("$.options", hasSize(2)));
    }
}
