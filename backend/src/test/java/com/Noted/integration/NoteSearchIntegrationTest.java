package com.Noted.integration;

import com.Noted.model.User;
import com.Noted.repository.CategoryRepository;
import com.Noted.repository.NoteRepository;
import com.Noted.repository.RefreshTokenRepository;
import com.Noted.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class NoteSearchIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private NoteRepository noteRepository;
    @Autowired private RefreshTokenRepository refreshTokenRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private static final String USER_A_EMAIL = "searchtest.a@test.com";
    private static final String USER_B_EMAIL = "searchtest.b@test.com";
    private static final String PASSWORD = "password123";

    @BeforeEach
    void setUp() {
        cleanupUser(USER_A_EMAIL);
        cleanupUser(USER_B_EMAIL);
        createUser(USER_A_EMAIL);
        createUser(USER_B_EMAIL);
    }

    private void cleanupUser(String email) {
        userRepository.findByEmail(email).ifPresent(existingUser -> {
            refreshTokenRepository.deleteAllByUser(existingUser);
            refreshTokenRepository.flush();
            noteRepository.deleteAll(noteRepository.findAllByUserId(existingUser.getId()));
            noteRepository.flush();
            userRepository.delete(existingUser);
            userRepository.flush();
        });
    }

    private void createUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setName("Search Tester");
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

    // ---------- BASIC MATCHING ----------

    @Test
    void search_matchesTitle_returnsNote() throws Exception {
        String token = loginAndGetAccessToken(USER_A_EMAIL);
        createNoteAndGetId(token, "Quarterly Planning", "some unrelated content");

        mockMvc.perform(get("/notes/search")
                        .param("query", "quarterly")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Quarterly Planning"));
    }

    @Test
    void search_matchesBody_returnsNote() throws Exception {
        String token = loginAndGetAccessToken(USER_A_EMAIL);
        createNoteAndGetId(token, "Random title", "remember to buy groceries tomorrow");

        mockMvc.perform(get("/notes/search")
                        .param("query", "groceries")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void search_isCaseInsensitive() throws Exception {
        String token = loginAndGetAccessToken(USER_A_EMAIL);
        createNoteAndGetId(token, "Meeting Notes", "discussed roadmap");

        mockMvc.perform(get("/notes/search")
                        .param("query", "MEETING")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void search_matchesStemmedWord() throws Exception {
        // "running" should match a search for "run" via to_tsvector stemming
        String token = loginAndGetAccessToken(USER_A_EMAIL);
        createNoteAndGetId(token, "Training log", "went running this morning");

        mockMvc.perform(get("/notes/search")
                        .param("query", "run")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // ---------- RANKING ----------

    @Test
    void search_titleMatchRanksAboveBodyOnlyMatch() throws Exception {
        String token = loginAndGetAccessToken(USER_A_EMAIL);
        createNoteAndGetId(token, "unrelated", "this note mentions budget somewhere in the body");
        createNoteAndGetId(token, "Budget Report", "unrelated content");

        mockMvc.perform(get("/notes/search")
                        .param("query", "budget")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Budget Report"));
    }

    // ---------- NO RESULTS / EDGE CASES ----------

    @Test
    void search_noMatches_returnsEmptyList() throws Exception {
        String token = loginAndGetAccessToken(USER_A_EMAIL);
        createNoteAndGetId(token, "Some title", "some body");

        mockMvc.perform(get("/notes/search")
                        .param("query", "nonexistentxyz")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void search_specialCharactersInQuery_doesNotError() throws Exception {
        String token = loginAndGetAccessToken(USER_A_EMAIL);
        createNoteAndGetId(token, "Some title", "some body");

        mockMvc.perform(get("/notes/search")
                        .param("query", "test & | ! ( )")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void search_multiWordQuery_matchesRegardlessOfOrder() throws Exception {
        String token = loginAndGetAccessToken(USER_A_EMAIL);
        createNoteAndGetId(token, "Team sync", "notes about our meeting yesterday afternoon");

        mockMvc.perform(get("/notes/search")
                        .param("query", "meeting notes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // ---------- AUTH / OWNERSHIP ----------

    @Test
    void search_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/notes/search")
                        .param("query", "anything"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void search_onlyReturnsOwnNotes() throws Exception {
        String tokenA = loginAndGetAccessToken(USER_A_EMAIL);
        String tokenB = loginAndGetAccessToken(USER_B_EMAIL);

        createNoteAndGetId(tokenA, "Shared keyword note", "body");
        createNoteAndGetId(tokenB, "Shared keyword note", "body");

        mockMvc.perform(get("/notes/search")
                        .param("query", "shared")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // ---------- DELETED NOTES ----------

    @Test
    void search_excludesDeletedNotes() throws Exception {
        String token = loginAndGetAccessToken(USER_A_EMAIL);
        Long noteId = createNoteAndGetId(token, "Deleted note", "unique searchable phrase");

        mockMvc.perform(delete("/notes/delete/" + noteId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/notes/search")
                        .param("query", "searchable")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}