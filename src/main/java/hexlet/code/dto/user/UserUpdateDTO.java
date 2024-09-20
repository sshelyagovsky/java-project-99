package hexlet.code.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class UserUpdateDTO {

    private JsonNullable<String>  firstName;

    private JsonNullable<String>  lastName;

    @Email
    private JsonNullable<String> email;

    @NotBlank
    @Size(min = 3, max = 100)
    private JsonNullable<String> password;
}
