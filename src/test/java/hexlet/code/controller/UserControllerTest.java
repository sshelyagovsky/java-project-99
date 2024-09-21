package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = Instancio.of(modelGenerator.getUserModel())
                .create();

    }

    @Test
    public void testIndex() throws Exception {

        userRepository.save(testUser);

        var response = mockMvc.perform(get("/api/users").with(jwt()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        var body = response.getContentAsString();

        List<UserDTO> users = om.readValue(body, new TypeReference<>() {});
        var actual = users.stream().map(userMapper::map).toList();

        var expected = userRepository.findAll();
        assertThatJson(body).isArray();
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testShow() throws Exception {

        userRepository.save(testUser);

        var response = mockMvc.perform(get("/api/users/{id}"
                        , testUser.getId()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        var body = response.getContentAsString();

        assertThatJson(body).and(
                v -> v.node("id").isEqualTo(testUser.getId()),
                v -> v.node("email").isEqualTo(testUser.getEmail()),
                v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                v -> v.node("lastName").isEqualTo(testUser.getLastName()),
                v -> v.node("createdAt").isEqualTo(testUser.getCreatedAt().format(ModelGenerator.FORMATTER))
        );
    }

    @Test
    public void testCreate() throws Exception {

        var userData = Instancio.of(modelGenerator.getUserModel())
                .create();

        var request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userData));

        mockMvc.perform(request.with(jwt()))
                .andExpect(status().isCreated());

        var user = userRepository.findByEmail(userData.getEmail()).get();

        assertThat(user).isNotNull();
        assertThat(user.getFirstName()).isEqualTo(userData.getFirstName());
        assertThat(user.getLastName()).isEqualTo(userData.getLastName());
        assertThat(user.getEmail()).isEqualTo(userData.getEmail());
    }

    @Test
    public void testUpdate() throws Exception {

        userRepository.save(testUser);

        var dto = userMapper.map(testUser);
        dto.setEmail("s.shelyagovsky@gmail.com");
        dto.setFirstName("funny_test");

        var request = put("/api/users/" + testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request.with(jwt()))
                .andExpect(status().isOk());

        var user = userRepository.findByEmail(dto.getEmail()).get();

        assertThat(user).isNotNull();
        assertThat(user.getFirstName()).isEqualTo(dto.getFirstName());
        assertThat(user.getEmail()).isEqualTo(dto.getEmail());
        assertThat(user.getPassword()).isNotEqualTo(testUser.getPassword());
    }

    @Test
    public void testDelete() throws Exception {
        userRepository.save(testUser);

        var request = delete("/api/users/{id}", testUser.getId()).with(jwt());

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        var isExistUser = userRepository.existsById(testUser.getId());
        assertThat(isExistUser).isFalse();
    }
}
