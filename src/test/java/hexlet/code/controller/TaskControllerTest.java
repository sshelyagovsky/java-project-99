package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.util.ModelGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    private Task task;

    @BeforeEach
    public void setUp() {
        task = modelGenerator.generateTask();
    }

    @Test
    public void testIndex() throws Exception {

        taskRepository.save(task);

        var response = mockMvc.perform(get("/api/tasks").with(jwt()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        var body = response.getContentAsString();

        List<Task> tasks = om.readValue(body, new TypeReference<>() { });

        var expected = taskRepository.findAll();
        assertThat(tasks).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testIndexFilter() throws Exception {

        taskRepository.save(task);

        var titleCont = task.getName();
        var assigneeId = task.getAssignee().getId();
        var status = task.getTaskStatus().getSlug();
        var labelId = task.getLabels().stream().iterator().next().getId();

        var response = mockMvc.perform(get("/api/tasks" + "?"
                        + "titleCont=" + titleCont
                        + "&assigneeId=" + assigneeId
                        + "&status=" + status
                        + "&labelId=" + labelId)
                        .with(jwt()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        var body = response.getContentAsString();
        System.out.println("body -->" + body);
        var expected = new HashMap<>();

        expected.put("id", task.getId());
        expected.put("taskLabelIds", Set.of(labelId));
        expected.put("index", task.getIndex());
        expected.put("createdAt", task.getCreatedAt().format(ModelGenerator.FORMATTER));
        expected.put("status", task.getTaskStatus().getSlug());
        expected.put("assignee_id", task.getAssignee().getId());
        expected.put("title", task.getName());
        expected.put("content", task.getDescription());

        assertThatJson(body)
                .isArray()
                .contains(om.writeValueAsString(expected));
    }

    @Test
    public void testShow() throws Exception {

        taskRepository.save(task);

        var response = mockMvc.perform(get("/api/tasks/{id}",
                task.getId()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        var body = response.getContentAsString();

        assertThatJson(body).and(
                v -> v.node("id").isEqualTo(task.getId()),
                v -> v.node("index").isEqualTo(task.getIndex()),
                v -> v.node("title").isEqualTo(task.getName()),
                v -> v.node("content").isEqualTo(task.getDescription()),
                v -> v.node("status").isEqualTo(task.getTaskStatus().getSlug()),
                v -> v.node("assignee_id").isEqualTo(task.getAssignee().getId())
        );
    }

    @Test
    public void testCreate() throws Exception {

        var taskData = modelGenerator.generateTask();

        HashMap<Object, Object> taskRequestData = new HashMap<>();
        taskRequestData.put("index", taskData.getIndex());
        taskRequestData.put("status", taskData.getTaskStatus().getSlug());
        taskRequestData.put("assignee_id", taskData.getAssignee().getId());
        taskRequestData.put("title", taskData.getName());
        taskRequestData.put("content", taskData.getDescription());

        mockMvc.perform(post("/api/tasks")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(taskRequestData)))
                .andExpect(status().isCreated())
                .andReturn();

        var task = taskRepository.findByName(taskData.getName()).get();

        assertThat(task).isNotNull();
        assertThat(task.getIndex()).isEqualTo(taskData.getIndex());
        assertThat(task.getTaskStatus().getSlug()).isEqualTo(taskData.getTaskStatus().getSlug());
        assertThat(task.getAssignee().getId()).isEqualTo(taskData.getAssignee().getId());
        assertThat(task.getName()).isEqualTo(taskData.getName());
        assertThat(task.getDescription()).isEqualTo(taskData.getDescription());
    }

    @Test
    public void testUpdate() throws Exception {

        taskRepository.save(task);

        var dto = taskMapper.map(task);
        dto.setTitle("New title");
        dto.setContent("New Content");

        mockMvc.perform(put("/api/tasks/{id}", task.getId())
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn();
        var taskNew = taskRepository.findByName(dto.getTitle()).get();

        assertThat(taskNew).isNotNull();
        assertThat(taskNew.getName()).isEqualTo(dto.getTitle());
        assertThat(taskNew.getDescription()).isEqualTo(dto.getContent());
    }

    @Test
    public void testDelete() throws Exception {

        taskRepository.save(task);

        mockMvc.perform(delete("/api/tasks/{id}", task.getId())
                        .with(jwt()))
                .andExpect(status().isNoContent());

        var isExistTask = taskRepository.existsById(task.getId());

        assertThat(isExistTask).isFalse();
    }
}
