package com.hirehub.hirehub_api.security;

import com.hirehub.hirehub_api.entity.User;
import com.hirehub.hirehub_api.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user =  userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("This UserName was not found"));
        return new CustomUserDetails(user);
    }
}
