package hexlet.code.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.taskstatus.TaskStatusDTO;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
public class TaskStatusControllerTest {

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    private TaskStatus taskStatus;

    @BeforeEach
    public void setUp() {
        taskStatus = Instancio.of(modelGenerator.getTaskStatusModel())
                .create();
    }

    @Test
    public void testIndex() throws Exception {
        taskStatusRepository.save(taskStatus);

        var response = mockMvc.perform(get("/api/task_statuses").with(jwt()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        var body = response.getContentAsString();

        List<TaskStatusDTO> taskStatuses = om.readValue(body, new TypeReference<List<TaskStatusDTO>>() { });

        var actual = taskStatuses.stream().map(taskStatusMapper::map).toList();

        var expected = taskStatusRepository.findAll();

        assertThatJson(body).isArray();
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testShow() throws Exception {

        taskStatusRepository.save(taskStatus);

        var response = mockMvc.perform(get("/api/task_statuses/{id}",
                        taskStatus.getId()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        var body = response.getContentAsString();

        assertThatJson(body).and(
                v -> v.node("id").isEqualTo(taskStatus.getId()),
                v -> v.node("name").isEqualTo(taskStatus.getName()),
                v -> v.node("slug").isEqualTo(taskStatus.getSlug()),
                v -> v.node("createdAt").isEqualTo(taskStatus.getCreatedAt().format(ModelGenerator.FORMATTER))
        );
    }

    @Test
    public void testCreate() throws Exception {
        var taskStatusData = Instancio.of(modelGenerator.getTaskStatusModel()).create();

        mockMvc.perform(post("/api/task_statuses")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(taskStatusData)))
                .andExpect(status().isCreated())
                .andReturn();

        var taskStatus = taskStatusRepository.findTaskStatusBySlug(taskStatusData.getSlug()).get();

        assertThat(taskStatus).isNotNull();
        assertThat(taskStatus.getName()).isEqualTo(taskStatusData.getName());
        assertThat(taskStatus.getSlug()).isEqualTo(taskStatusData.getSlug());
    }

    @Test
    public void testUpdate() throws Exception {

        taskStatusRepository.save(taskStatus);

        var dto = taskStatusMapper.map(taskStatus);
        dto.setName("TestStatus");
        dto.setSlug("test_status");

        mockMvc.perform(put("/api/task_statuses/{id}",
                        taskStatus.getId())
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn();

        var taskStatusNew = taskStatusRepository.findById(dto.getId()).get();

        assertThat(taskStatusNew).isNotNull();
        assertThat(taskStatusNew.getName()).isEqualTo(dto.getName());
        assertThat(taskStatusNew.getSlug()).isEqualTo(dto.getSlug());
        assertThat(taskStatusNew.getSlug()).isNotEqualTo(taskStatus.getSlug());
    }

    @Test
    public void testDelete() throws Exception {

        taskStatusRepository.save(taskStatus);

        mockMvc.perform(delete("/api/task_statuses/{id}",
                        taskStatus.getId())
                        .with(jwt()))
                .andExpect(status().isNoContent());
        var isExistTaskStatus = taskStatusRepository.existsById(taskStatus.getId());

        assertThat(isExistTaskStatus).isFalse();
    }
}
