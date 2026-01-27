package com.postech.fiap.fase5.infrastructure.security.service;


import com.postech.fiap.fase5.api.dto.authentication.UserAuthenticatedDTO;
import com.postech.fiap.fase5.api.entities.UserRole;
import com.postech.fiap.fase5.api.usecases.user.UserFindByLoginUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserFindByLoginUseCase userFindByLoginUseCase;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        var user = userFindByLoginUseCase.findUserByLogin(login);
        return new UserAuthenticatedDTO(user.getId(), user.getName(), user.getPassword(), user.getRoles().stream().map(UserRole::getRole).toList());
    }
}