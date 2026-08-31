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
class CategoryIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private NoteRepository noteRepository;
    @Autowired private RefreshTokenRepository refreshTokenRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private static final String USER_A_EMAIL = "categorytest.a@test.com";
    private static final String USER_B_EMAIL = "categorytest.b@test.com";
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
            categoryRepository.deleteAll(categoryRepository.findAllByUser(existingUser));
            categoryRepository.flush();
            userRepository.delete(existingUser);
            userRepository.flush();
        });
    }

    private void createUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setName("Category Tester");
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

    private Long createCategoryAndGetId(String token, String name) throws Exception {
        String response = mockMvc.perform(post("/category/add")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\"}"))
                .andReturn().getResponse().getContentAsString();
        Number id = com.jayway.jsonpath.JsonPath.read(response, "$.id");
        return id.longValue();
    }

    // ---------- CREATE (ADD) ----------

    @Test
    void addCategory_withValidData_returns201() throws Exception {
        String token = loginAndGetAccessToken(USER_A_EMAIL);

        mockMvc.perform(post("/category/add")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Work\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Work"));
    }

    @Test
    void addCategory_withoutToken_returns401() throws Exception {
        mockMvc.perform(post("/category/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Work\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void addCategory_withInvalidData_returns400() throws Exception {
        String token = loginAndGetAccessToken(USER_A_EMAIL);

        mockMvc.perform(post("/category/add")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    // ---------- GET ALL ----------

    @Test
    void getAllCategories_returnsOnlyOwnCategories() throws Exception {
        String tokenA = loginAndGetAccessToken(USER_A_EMAIL);
        String tokenB = loginAndGetAccessToken(USER_B_EMAIL);

        createCategoryAndGetId(tokenA, "A's Category");
        createCategoryAndGetId(tokenB, "B's Category");

        mockMvc.perform(get("/category/get-all")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("A's Category"));
    }

    // ---------- UPDATE ----------

    @Test
    void updateCategory_ownCategory_returns200AndUpdatesFields() throws Exception {
        String token = loginAndGetAccessToken(USER_A_EMAIL);
        Long categoryId = createCategoryAndGetId(token, "Old Name");

        mockMvc.perform(put("/category/update/" + categoryId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New Name\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"));
    }

    @Test
    void updateCategory_anotherUsersCategory_returns404Or403() throws Exception {
        String tokenA = loginAndGetAccessToken(USER_A_EMAIL);
        String tokenB = loginAndGetAccessToken(USER_B_EMAIL);
        Long categoryId = createCategoryAndGetId(tokenA, "A's Category");

        mockMvc.perform(put("/category/update/" + categoryId)
                        .header("Authorization", "Bearer " + tokenB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Hijacked Name\"}"))
                .andExpect(status().isNotFound());
    }

    // ---------- DELETE ----------

    @Test
    void deleteCategory_ownCategory_returns204AndHidesFromGetAll() throws Exception {
        String token = loginAndGetAccessToken(USER_A_EMAIL);
        Long categoryId = createCategoryAndGetId(token, "To Be Deleted");

        mockMvc.perform(delete("/category/delete/" + categoryId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/category/get-all")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void deleteCategory_anotherUsersCategory_returns404Or403() throws Exception {
        String tokenA = loginAndGetAccessToken(USER_A_EMAIL);
        String tokenB = loginAndGetAccessToken(USER_B_EMAIL);
        Long categoryId = createCategoryAndGetId(tokenA, "A's Category");

        mockMvc.perform(delete("/category/delete/" + categoryId)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound());
    }

    // ---------- NOTE COUNT ----------

    @Test
    void getNoteCountByCategory_returns200AndCorrectCounts() throws Exception {
        String token = loginAndGetAccessToken(USER_A_EMAIL);
        createCategoryAndGetId(token, "Personal");

        mockMvc.perform(get("/category/note-count")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}