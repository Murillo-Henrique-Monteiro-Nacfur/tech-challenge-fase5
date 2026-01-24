package coding.interview.app.api.controllers;

import coding.interview.app.api.dto.authentication.LoginRequestDTO;
import coding.interview.app.api.dto.authentication.LoginResponseDTO;
import coding.interview.app.api.presenter.LoginPresenter;
import coding.interview.app.api.services.authentication.AuthenticationService;
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
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final LoginPresenter loginPresenter;

    @Operation(summary = "Authenticate a user and return a JWT")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
        var token = authenticationService.authenticate(loginRequestDTO.getLogin(), loginRequestDTO.getPassword());
        return ResponseEntity.ok(loginPresenter.presentLoginSuccess(token));
    }
}