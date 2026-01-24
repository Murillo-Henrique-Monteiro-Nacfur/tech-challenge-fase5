package com.postech.fiap.fase5.api.services.user;

import com.postech.fiap.fase5.api.entities.Users;
import com.postech.fiap.fase5.api.repositories.UserRepository;
import com.postech.fiap.fase5.infrastructure.exceptions.ApplicationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Users findUserByLogin(String login) {
        return userRepository.findByLogin(login).orElseThrow(() -> new ApplicationNotFoundException("User not found"));
    }

}
