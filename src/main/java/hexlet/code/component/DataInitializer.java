package hexlet.code.component;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.LabelService;
import hexlet.code.service.TaskStatusService;
import hexlet.code.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;

    private final TaskStatusRepository taskStatusRepository;

    private final UserService userService;

    private final TaskStatusService taskStatusService;

    private final LabelRepository labelRepository;

    private final LabelService labelService;

    @Override
    public void run(ApplicationArguments args) {

        var email = "hexlet@example.com";

        if (userRepository.findByEmail(email).isEmpty()) {
            var userData = new UserCreateDTO();
            userData.setFirstName("hexlet");
            userData.setLastName("hexlet");
            userData.setEmail(email);
            userData.setPassword("qwerty");
            userService.create(userData);
        }

        HashMap<String, String> taskStatuses = new HashMap<>();

        taskStatuses.put("Draft", "draft");
        taskStatuses.put("ToReview", "to_review");
        taskStatuses.put("ToBeFixed", "to_be_fixed");
        taskStatuses.put("ToPublish", "to_publish");
        taskStatuses.put("Published", "published");

        for (var status : taskStatuses.entrySet()) {
            if (taskStatusRepository.findTaskStatusBySlug(status.getValue()).isEmpty()) {
               var taskStatus = new TaskStatusCreateDTO();
               taskStatus.setName(status.getKey());
               taskStatus.setSlug(status.getValue());
               taskStatusService.create(taskStatus);
            }
        }

        var labels = List.of("feature", "bug");

        for (var label : labels) {
            if (labelRepository.findByName(label).isEmpty()) {
                var labelNew = new LabelCreateDTO();
                labelNew.setName(label);
                labelService.create(labelNew);
            }
        }
    }
}
