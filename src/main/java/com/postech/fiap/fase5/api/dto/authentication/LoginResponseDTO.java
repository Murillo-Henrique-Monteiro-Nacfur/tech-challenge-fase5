package coding.interview.app.api.dto.authentication;


import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponseDTO(
        @Schema(description = "JWT token", example = "eyJhbGciOiJIUzI1NiJ9")
        String token) {
}
