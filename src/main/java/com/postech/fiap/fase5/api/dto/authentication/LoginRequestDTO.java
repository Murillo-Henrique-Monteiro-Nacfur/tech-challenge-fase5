package com.postech.fiap.fase5.api.dto.authentication;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDTO {
    @NotEmpty
    @Schema(description = "Login of the user", example = "admin")
    private String login;
    @NotEmpty
    @Schema(description = "Password of the user", example = "admin")
    private String password;
}
