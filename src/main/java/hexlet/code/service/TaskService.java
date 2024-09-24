package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskMapper taskMapper;

    public List<TaskDTO> findAll() {
        var tasks = taskRepository.findAll();

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
