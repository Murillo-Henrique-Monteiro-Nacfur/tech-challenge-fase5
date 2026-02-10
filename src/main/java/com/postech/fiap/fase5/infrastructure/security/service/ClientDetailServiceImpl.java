package com.postech.fiap.fase5.infrastructure.security.service;

import com.postech.fiap.fase5.api.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientDetailServiceImpl implements UserDetailsService {

    private final ClientRepository clientRepository;

    @Override
    public UserDetails loadUserByUsername(String clientId) throws UsernameNotFoundException {
        return clientRepository.findByClientId(clientId)
                .map(ClientUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Client not found with clientId: " + clientId));
    }
}
