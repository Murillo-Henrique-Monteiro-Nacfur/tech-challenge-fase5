package coding.interview.app.api.services.user;

import coding.interview.app.api.entities.Users;
import coding.interview.app.api.repositories.UserRepository;
import coding.interview.app.infrastructure.exceptions.SecureFlightNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Users findUserByLogin(String login) {
        return userRepository.findByLogin(login).orElseThrow(() -> new SecureFlightNotFoundException("User not found"));
    }

}
