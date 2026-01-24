package com.postech.fiap.fase5.api.presenter;

import com.postech.fiap.fase5.api.dto.authentication.LoginResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class LoginPresenter {

    public LoginResponseDTO presentLoginSuccess(String token) {
        return new LoginResponseDTO(token);
    }

}
