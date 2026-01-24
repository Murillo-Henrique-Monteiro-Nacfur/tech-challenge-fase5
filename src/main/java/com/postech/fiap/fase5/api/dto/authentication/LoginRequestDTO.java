package coding.interview.app.api.dto.authentication;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginRequestDTO {
    @NotEmpty
    @Schema(description = "Login of the user", example = "admin")
    private String login;
    @NotEmpty
    @Schema(description = "Password of the user", example = "admin")
    private String password;
}
