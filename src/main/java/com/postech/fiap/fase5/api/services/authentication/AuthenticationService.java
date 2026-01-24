package coding.interview.app.api.services.authentication;

import coding.interview.app.infrastructure.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public String authenticate(String login, String password){
        var authenticationToken = new UsernamePasswordAuthenticationToken(login, password);
        Authentication authenticated = authenticationManager.authenticate(authenticationToken);
        return jwtService.generateToken(authenticated);
    }
}