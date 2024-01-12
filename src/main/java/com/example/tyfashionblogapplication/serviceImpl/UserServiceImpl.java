package com.example.tyfashionblogapplication.serviceImpl;

import com.example.tyfashionblogapplication.DTO.UserDto;
import com.example.tyfashionblogapplication.enums.Role;
import com.example.tyfashionblogapplication.model.User;
import com.example.tyfashionblogapplication.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserDetailsService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("Username Not Found"));
    }


    public User saveUser(UserDto userDto) {
        User user = new ObjectMapper().convertValue(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setUserRole(Role.ROLE_USER);
        return userRepository.save(user);
    }
    public User saveAdmin(UserDto userDto) {
        User admin = new ObjectMapper().convertValue(userDto, User.class);
        admin.setPassword(passwordEncoder.encode(userDto.getPassword()));
        admin.setUserRole(Role.ROLE_ADMIN);
        return userRepository.save(admin);
    }
}
