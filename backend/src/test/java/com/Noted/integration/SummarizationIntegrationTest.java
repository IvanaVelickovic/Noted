package com.Noted.integration;

import com.Noted.client.SummarizationClient;
import com.Noted.messaging.RabbitMQConsumer;
import com.Noted.model.User;
import com.Noted.model.enums.SummaryJobStatus;
import com.Noted.repository.NoteRepository;
import com.Noted.repository.RefreshTokenRepository;
import com.Noted.repository.SummaryJobRepository;
import com.Noted.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SummarizationIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private NoteRepository noteRepository;
    @Autowired private RefreshTokenRepository refreshTokenRepository;
    @Autowired private SummaryJobRepository summaryJobRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private RabbitMQConsumer rabbitMQConsumer;

    @MockitoBean
    private SummarizationClient summarizationClient;

    private static final String USER_A_EMAIL = "sumtest.a@test.com";
    private static final String USER_B_EMAIL = "sumtest.b@test.com";
    private static final String PASSWORD = "password123";

    @BeforeEach
    void setUp() {
        cleanupUser(USER_A_EMAIL);
        cleanupUser(USER_B_EMAIL);
        createUser(USER_A_EMAIL);
        createUser(USER_B_EMAIL);
    }

    private void cleanupUser(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            refreshTokenRepository.deleteAllByUser(user);
            refreshTokenRepository.flush();
            summaryJobRepository.deleteAll(summaryJobRepository.findAllByNote_User_Id(user.getId()));
            summaryJobRepository.flush();
            noteRepository.deleteAll(noteRepository.findAllByUserId(user.getId()));
            noteRepository.flush();
            userRepository.delete(user);
            userRepository.flush();
        });
    }

    private void createUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setName("Summarization Tester");
        user.setPasswordHash(passwordEncoder.encode(PASSWORD));
        userRepository.save(user);
    }

    private String loginAndGetAccessToken(String email) throws Exception {
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + PASSWORD + "\"}"))
                .andReturn().getResponse().getContentAsString();
        return com.jayway.jsonpath.JsonPath.read(response, "$.accessToken");
    }

    private Long createNoteAndGetId(String token, String title, String body) throws Exception {
        String response = mockMvc.perform(post("/notes/create")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"" + title + "\",\"body\":\"" + body + "\"}"))
                .andReturn().getResponse().getContentAsString();
        Number id = com.jayway.jsonpath.JsonPath.read(response, "$.id");
        return id.longValue();
    }

    // ---------- POST /{noteId}/summarize ----------

    @Test
    void summarizeNote_validNote_returns202AcceptedAndPendingJob() throws Exception {
        String token = loginAndGetAccessToken(USER_A_EMAIL);
        Long noteId = createNoteAndGetId(token, "Study Notes", "This is a detailed note about Java and Spring Boot.");

        mockMvc.perform(post("/notes/" + noteId + "/summarize")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value(SummaryJobStatus.PENDING.name()))
                .andExpect(jsonPath("$.jobId").exists());
    }

    @Test
    void summarizeNote_withoutToken_returns401() throws Exception {
        mockMvc.perform(post("/notes/123/summarize"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void summarizeNote_anotherUsersNote_returns404() throws Exception {
        String tokenA = loginAndGetAccessToken(USER_A_EMAIL);
        String tokenB = loginAndGetAccessToken(USER_B_EMAIL);
        Long noteIdA = createNoteAndGetId(tokenA, "User A Note", "Private content");

        mockMvc.perform(post("/notes/" + noteIdA + "/summarize")
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound());
    }

    @Test
    void summarizeNote_nonExistentNote_returns404() throws Exception {
        String token = loginAndGetAccessToken(USER_A_EMAIL);

        mockMvc.perform(post("/notes/999999/summarize")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    // ---------- GET /{id} (JOB BY ID) ----------

    @Test
    void getJob_ownJob_returns200() throws Exception {
        String token = loginAndGetAccessToken(USER_A_EMAIL);
        Long noteId = createNoteAndGetId(token, "Note Title", "Note content for summary");

        String createResponse = mockMvc.perform(post("/notes/" + noteId + "/summarize")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isAccepted())
                .andReturn().getResponse().getContentAsString();

        Number jobId = com.jayway.jsonpath.JsonPath.read(createResponse, "$.jobId");

        mockMvc.perform(get("/jobs/" + jobId.longValue())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(jobId.longValue()))
                .andExpect(jsonPath("$.status").value(SummaryJobStatus.PENDING.name()));
    }

    @Test
    void getJob_anotherUsersJob_returns404() throws Exception {
        String tokenA = loginAndGetAccessToken(USER_A_EMAIL);
        String tokenB = loginAndGetAccessToken(USER_B_EMAIL);
        Long noteId = createNoteAndGetId(tokenA, "User A Note", "Content");

        String createResponse = mockMvc.perform(post("/notes/" + noteId + "/summarize")
                        .header("Authorization", "Bearer " + tokenA))
                .andReturn().getResponse().getContentAsString();

        Number jobId = com.jayway.jsonpath.JsonPath.read(createResponse, "$.jobId");

        mockMvc.perform(get("/summary-jobs/" + jobId.longValue())
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound());
    }

    // ---------- GET /{noteId}/summary-history ----------

    @Test
    void getNoteSummaryHistory_returnsOrderedJobs() throws Exception {
        String token = loginAndGetAccessToken(USER_A_EMAIL);
        Long noteId = createNoteAndGetId(token, "History Note", "Body text for history test");

        mockMvc.perform(post("/notes/" + noteId + "/summarize").header("Authorization", "Bearer " + token));
        mockMvc.perform(post("/notes/" + noteId + "/summarize").header("Authorization", "Bearer " + token));

        mockMvc.perform(get("/notes/" + noteId + "/summary-history")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getNoteSummaryHistory_anotherUsersNote_returns404() throws Exception {
        String tokenA = loginAndGetAccessToken(USER_A_EMAIL);
        String tokenB = loginAndGetAccessToken(USER_B_EMAIL);
        Long noteId = createNoteAndGetId(tokenA, "Private History Note", "Body text");

        mockMvc.perform(get("/notes/" + noteId + "/summary-history")
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound());
    }

    // ---------- QUEUE CONSUMPTION & RETRY FLOWS ----------

    @Test
    void queueConsumer_successfulApiCall_completesJob() throws Exception {
        String token = loginAndGetAccessToken(USER_A_EMAIL);
        Long noteId = createNoteAndGetId(token, "AI Note", "Complete this summary.");

        when(summarizationClient.summarize(anyString())).thenReturn("This is the generated AI summary.");

        String createResponse = mockMvc.perform(post("/notes/" + noteId + "/summarize")
                        .header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString();
        Number jobId = com.jayway.jsonpath.JsonPath.read(createResponse, "$.jobId");

        // Simulate RabbitMQ receiving the message
        com.Noted.dto.SummaryJobMessage message = new com.Noted.dto.SummaryJobMessage(jobId.longValue(), "Complete this summary.");
        rabbitMQConsumer.receiveMessage(message);

        mockMvc.perform(get("/jobs/" + jobId.longValue())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(SummaryJobStatus.COMPLETED.name()))
                .andExpect(jsonPath("$.result").value("This is the generated AI summary."));
    }

    @Test
    void queueConsumer_firstAttemptTimeout_updatesErrorMessageAndRetries() throws Exception {
        String token = loginAndGetAccessToken(USER_A_EMAIL);
        Long noteId = createNoteAndGetId(token, "Timeout Note", "Note content");

        when(summarizationClient.summarize(anyString()))
                .thenThrow(new org.springframework.web.client.ResourceAccessException("Read timed out"));

        String createResponse = mockMvc.perform(post("/notes/" + noteId + "/summarize")
                        .header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString();
        Number jobId = com.jayway.jsonpath.JsonPath.read(createResponse, "$.jobId");

        // Simulate Attempt 1 failure
        com.Noted.dto.SummaryJobMessage message = new com.Noted.dto.SummaryJobMessage(jobId.longValue(), "Note content");
        rabbitMQConsumer.receiveMessage(message);

        mockMvc.perform(get("/jobs/" + jobId.longValue())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(SummaryJobStatus.PROCESSING.name()))
                .andExpect(jsonPath("$.errorMessage", containsString("Attempt 1 of 3 failed")));
    }

    @Test
    void queueConsumer_exceedsMaxRetries_marksJobAsFailed() throws Exception {
        String token = loginAndGetAccessToken(USER_A_EMAIL);
        Long noteId = createNoteAndGetId(token, "Failing Note", "Note content");

        when(summarizationClient.summarize(anyString()))
                .thenThrow(new RuntimeException("Gemini API Overloaded"));

        String createResponse = mockMvc.perform(post("/notes/" + noteId + "/summarize")
                        .header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString();
        Number jobId = com.jayway.jsonpath.JsonPath.read(createResponse, "$.jobId");

        com.Noted.dto.SummaryJobMessage message = new com.Noted.dto.SummaryJobMessage(jobId.longValue(), "Note content");

        // Simulate 3 consecutive queue failures
        rabbitMQConsumer.receiveMessage(message); // Attempt 1
        rabbitMQConsumer.receiveMessage(message); // Attempt 2
        rabbitMQConsumer.receiveMessage(message); // Attempt 3
        rabbitMQConsumer.receiveMessage(message); // Attempt 4 (Exceeds max)

        mockMvc.perform(get("/jobs/" + jobId.longValue())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(SummaryJobStatus.FAILED.name()))
                .andExpect(jsonPath("$.errorMessage", containsString("Failed to summarize note after 3 attempts")));
    }
}