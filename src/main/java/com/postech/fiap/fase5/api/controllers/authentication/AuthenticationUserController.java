package com.postech.fiap.fase5.api.controllers.authentication;


import com.postech.fiap.fase5.api.dto.authentication.LoginRequestDTO;
import com.postech.fiap.fase5.api.dto.authentication.LoginResponseDTO;
import com.postech.fiap.fase5.api.presenter.LoginPresenter;
import com.postech.fiap.fase5.api.usecases.authentication.UserAuthenticationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authentication")
@Tag(name = "Authentication")
@RequiredArgsConstructor
public class AuthenticationUserController {

    private final UserAuthenticationUseCase userAuthenticationUseCase;
    private final LoginPresenter loginPresenter;

    @Operation(summary = "Authenticate a user and return a JWT")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
        String token = userAuthenticationUseCase.execute(loginRequestDTO.getLogin(), loginRequestDTO.getPassword());
        return ResponseEntity.ok(loginPresenter.presentLoginSuccess(token));
    }
}