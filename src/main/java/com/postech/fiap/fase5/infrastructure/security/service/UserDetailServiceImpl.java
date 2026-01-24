package com.project.email_expenses_service.infrastructure.security.service;

import com.project.email_expenses_service.api.dto.authentication.UserAuthenticatedDTO;
import com.project.email_expenses_service.api.models.UserRole;
import com.project.email_expenses_service.api.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        var user = userService.findUserByLogin(login);
        return new UserAuthenticatedDTO(user.getId(), user.getName(), user.getPassword(), user.getRoles().stream().map(UserRole::getRole).toList());
    }
}