package hexlet.code.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirement(name = "bearerAuth")
@Hidden
public class WelcomeController {
    @GetMapping(path = "/welcome")
    public String welcome() {
        return "Welcome to Spring";
    }
}
