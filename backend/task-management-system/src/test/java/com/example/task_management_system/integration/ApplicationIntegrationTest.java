package com.example.task_management_system.integration;

import com.example.task_management_system.project.ProjectRepository;
import com.example.task_management_system.project.membership.ProjectMembershipRepository;
import com.example.task_management_system.user.UserRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class ApplicationIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("tms_test")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMembershipRepository membershipRepository;

    @Test
    void shouldRegisterAndLoginUser() throws Exception {
        registerUser("integration@example.com");

        String response = mockMvc.perform(post("/api/auth/login")
                                                  .contentType(MediaType.APPLICATION_JSON)
                                                  .content(loginRequest("integration@example.com")))
                                 .andExpect(status().isOk())
                                 .andExpect(jsonPath("$.token").isNotEmpty())
                                 .andReturn()
                                 .getResponse()
                                 .getContentAsString();

        assertThat(userRepository.existsByEmail("integration@example.com")).isTrue();
        assertThat(JsonPath.<String>read(response, "$.token")).isNotBlank();
    }

    @Test
    void shouldRejectRequestWithoutToken() throws Exception {
        mockMvc.perform(get("/api/projects/my"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldCreateProjectForAuthenticatedUser() throws Exception {
        String email = "project.owner@example.com";
        registerUser(email);
        String token = login(email);

        String response = mockMvc.perform(post("/api/projects")
                                                  .header("Authorization", "Bearer " + token)
                                                  .contentType(MediaType.APPLICATION_JSON)
                                                  .content("""
                                                          {
                                                            "key": "INT",
                                                            "name": "Integration project",
                                                            "description": "Created in integration test"
                                                          }
                                                          """))
                                 .andExpect(status().isCreated())
                                 .andExpect(jsonPath("$.key").value("INT"))
                                 .andExpect(jsonPath("$.name").value("Integration project"))
                                 .andReturn()
                                 .getResponse()
                                 .getContentAsString();

        String projectId = JsonPath.read(response, "$.id");
        assertThat(projectRepository.existsByKey("INT")).isTrue();
        assertThat(membershipRepository.existsByProjectIdAndUserId(
                java.util.UUID.fromString(projectId),
                userRepository.findByEmailWithRoles(email).orElseThrow().getId()
        )).isTrue();
    }

    private void registerUser(String email) throws Exception {
        mockMvc.perform(post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                         {
                                           "email": "%s",
                                           "password": "password123",
                                           "firstName": "Test",
                                           "lastName": "User"
                                         }
                                         """.formatted(email)))
               .andExpect(status().isCreated());
    }

    private String login(String email) throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                                                  .contentType(MediaType.APPLICATION_JSON)
                                                  .content(loginRequest(email)))
                                 .andExpect(status().isOk())
                                 .andReturn()
                                 .getResponse()
                                 .getContentAsString();

        return JsonPath.read(response, "$.token");
    }

    private String loginRequest(String email) {
        return """
                {
                  "email": "%s",
                  "password": "password123"
                }
                """.formatted(email);
    }
}
