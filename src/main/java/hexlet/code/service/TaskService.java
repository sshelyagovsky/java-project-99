package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskParamsDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.repository.TaskRepository;
import hexlet.code.specification.TaskSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    private final TaskSpecification taskSpecification;

    public List<TaskDTO> findAll(TaskParamsDTO params, int page) {
        var spec = taskSpecification.build(params);
        var tasks = taskRepository.findAll(spec, PageRequest.of(page - 1, 10));

        return tasks.stream()
                .map(taskMapper::map)
                .toList();
    }

    public TaskDTO findById(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        var taskDTO = taskMapper.map(task);

        return taskDTO;
    }

    public TaskDTO create(TaskCreateDTO taskCreateData) {
        var task = taskMapper.create(taskCreateData);
        taskRepository.save(task);
        var taskDTO = taskMapper.map(task);

        return taskDTO;
    }

    public TaskDTO update(TaskUpdateDTO taskUpdateData, Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        taskMapper.update(taskUpdateData, task);
        taskRepository.save(task);
        var taskDTO = taskMapper.map(task);

        return taskDTO;
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }
}
