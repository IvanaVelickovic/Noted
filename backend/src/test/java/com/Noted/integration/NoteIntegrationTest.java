package com.Noted.integration;

import com.Noted.model.User;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class NoteIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private NoteRepository noteRepository;
    @Autowired private RefreshTokenRepository refreshTokenRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private static final String USER_A_EMAIL = "notetest.a@test.com";
    private static final String USER_B_EMAIL = "notetest.b@test.com";
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
        user.setName("Note Tester");
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

    // ---------- CREATE ----------

    @Test
    void createNote_withValidData_returns201() throws Exception {
        String token = loginAndGetAccessToken(USER_A_EMAIL);

        mockMvc.perform(post("/notes/create")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"My First Note\",\"body\":\"Hello world\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("My First Note"));
    }

    @Test
    void createNote_withoutToken_returns401() throws Exception {
        mockMvc.perform(post("/notes/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"My First Note\",\"body\":\"Hello world\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createNote_withBlankTitle_returns400() throws Exception {
        String token = loginAndGetAccessToken(USER_A_EMAIL);

        mockMvc.perform(post("/notes/create")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"body\":\"Hello world\"}"))
                .andExpect(status().isBadRequest());
    }

    // ---------- GET ALL ----------

    @Test
    void getAllNotes_returnsOnlyOwnNotes() throws Exception {
        String tokenA = loginAndGetAccessToken(USER_A_EMAIL);
        String tokenB = loginAndGetAccessToken(USER_B_EMAIL);

        createNoteAndGetId(tokenA, "A's note", "body");
        createNoteAndGetId(tokenB, "B's note", "body");

        mockMvc.perform(get("/notes/get-all")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("A's note"));
    }

    // ---------- GET BY ID ----------

    @Test
    void getNoteById_ownNote_returns200() throws Exception {
        String token = loginAndGetAccessToken(USER_A_EMAIL);
        Long noteId = createNoteAndGetId(token, "Owned note", "body");

        mockMvc.perform(get("/notes/get/" + noteId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Owned note"));
    }

    @Test
    void getNoteById_anotherUsersNote_returns404() throws Exception {
        String tokenA = loginAndGetAccessToken(USER_A_EMAIL);
        String tokenB = loginAndGetAccessToken(USER_B_EMAIL);
        Long noteId = createNoteAndGetId(tokenA, "A's note", "body");

        mockMvc.perform(get("/notes/get/" + noteId)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound());
    }

    @Test
    void getNoteById_nonExistentId_returns404() throws Exception {
        String token = loginAndGetAccessToken(USER_A_EMAIL);

        mockMvc.perform(get("/notes/get/999999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    // ---------- UPDATE ----------

    @Test
    void updateNote_ownNote_returns200AndUpdatesFields() throws Exception {
        String token = loginAndGetAccessToken(USER_A_EMAIL);
        Long noteId = createNoteAndGetId(token, "Original title", "original body");

        mockMvc.perform(post("/notes/update/" + noteId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated title\",\"body\":\"updated body\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated title"));
    }

    @Test
    void updateNote_anotherUsersNote_returns404() throws Exception {
        String tokenA = loginAndGetAccessToken(USER_A_EMAIL);
        String tokenB = loginAndGetAccessToken(USER_B_EMAIL);
        Long noteId = createNoteAndGetId(tokenA, "A's note", "body");

        mockMvc.perform(post("/notes/update/" + noteId)
                        .header("Authorization", "Bearer " + tokenB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Hijacked\",\"body\":\"hijacked body\"}"))
                .andExpect(status().isNotFound());
    }

    // ---------- DELETE ----------

    @Test
    void deleteNote_ownNote_returns204AndHidesFromGetAll() throws Exception {
        String token = loginAndGetAccessToken(USER_A_EMAIL);
        Long noteId = createNoteAndGetId(token, "To be deleted", "body");

        mockMvc.perform(post("/notes/delete/" + noteId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/notes/get-all")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void deleteNote_anotherUsersNote_returns404() throws Exception {
        String tokenA = loginAndGetAccessToken(USER_A_EMAIL);
        String tokenB = loginAndGetAccessToken(USER_B_EMAIL);
        Long noteId = createNoteAndGetId(tokenA, "A's note", "body");

        mockMvc.perform(post("/notes/delete/" + noteId)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound());
    }
}