package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
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
public class LabelControllerTest {

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    private Label label;


    @BeforeEach
    public void setUp() {
        label = Instancio.of(modelGenerator.getLabelModel()).create();
    }

    @Test
    public void testIndex() throws Exception {
        labelRepository.save(label);

        var response = mockMvc.perform(get("/api/labels").with(jwt()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        var body = response.getContentAsString();

        List<Label> labels = om.readValue(body, new TypeReference<>() { });

        var expected = labelRepository.findAll();

        assertThatJson(body).isArray();
        assertThat(labels).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testShow() throws Exception {

        labelRepository.save(label);

        var response = mockMvc.perform(get("/api/labels/{id}",
                label.getId()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        var body = response.getContentAsString();

        assertThatJson(body).and(
                v -> v.node("id").isEqualTo(label.getId()),
                v -> v.node("name").isEqualTo(label.getName()),
                v -> v.node("createdAt").isEqualTo(label.getCreatedAt().format(ModelGenerator.FORMATTER))
        );
    }

    @Test
    public void testCreate() throws Exception {
        var labelData = Instancio.of(modelGenerator.getLabelModel()).create();

        mockMvc.perform(post("/api/labels")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(labelData)))
                .andExpect(status().isCreated())
                .andReturn();

        var label = labelRepository.findByName(labelData.getName()).get();

        assertThat(label).isNotNull();
        assertThat(label.getName()).isEqualTo(labelData.getName());
    }

    @Test
    public void testUpdate() throws Exception {

        labelRepository.save(label);

        var labelData = new HashMap<>();
        labelData.put("name", "test_label");

        mockMvc.perform(put("/api/labels/{id}",
                        label.getId())
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(labelData)))
                .andExpect(status().isOk())
                .andReturn();

        var labelNew = labelRepository.findById(label.getId()).get();

        assertThat(labelNew).isNotNull();
        assertThat(labelNew.getName()).isEqualTo(labelData.get("name"));
    }

    @Test
    public void testDelete() throws Exception {

        labelRepository.save(label);

        mockMvc.perform(delete("/api/labels/{id}",
                        label.getId())
                        .with(jwt()))
                .andExpect(status().isNoContent())
                .andReturn();

        var isLabelExist = labelRepository.existsById(label.getId());
        assertThat(isLabelExist).isFalse();
    }
}
