package hexlet.code.component;

import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.TaskStatusService;
import hexlet.code.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskStatusService taskStatusService;

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
    }
}
