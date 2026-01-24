package coding.interview.app.api.presenter;

import coding.interview.app.api.dto.authentication.LoginResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class LoginPresenter {

    public LoginResponseDTO presentLoginSuccess(String token) {
        return new LoginResponseDTO(token);
    }

}
