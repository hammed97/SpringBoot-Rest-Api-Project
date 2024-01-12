package com.example.tyfashionblogapplication.utils;


import com.example.tyfashionblogapplication.DTO.UserDto;
import com.example.tyfashionblogapplication.enums.Role;
import com.example.tyfashionblogapplication.model.User;
import com.example.tyfashionblogapplication.repositories.UserRepository;
import com.example.tyfashionblogapplication.serviceImpl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.function.Function;

@Component
public class GoggleJwtUtils {
    @Value("${CLIENT.ID}")
    private final String CLIENT_ID="368163805710-0po6g0erk0m8nlvm8otfg0sl930su79s.apps.googleusercontent.com";
    private UserRepository userRepository;
    private UserServiceImpl userService;
    private PasswordEncoder passwordEncoder;
    private JwtUtils utils;

    public GoggleJwtUtils(UserRepository userRepository, UserServiceImpl userService, PasswordEncoder passwordEncoder, JwtUtils utils) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.utils = utils;
    }

    private final Function<String, UserDto> getUserFromIdToken = (token)-> {
        HttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = new GsonFactory();

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();
        GoogleIdToken idToken = null;
        try {
            idToken = verifier.verify(token);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            String userId = payload.getSubject();
            System.out.println("User ID: " + userId);

            String email = payload.getEmail();
//            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
//            String name = (String) payload.get("name");
//            String pictureUrl = (String) payload.get("picture");
//            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");
            return UserDto
                    .builder()
                    .username(email)
                    .firstName(givenName)
                    .lastName(familyName)
                    .password("GOOGLELOGIN")
                    .build();

        }
        return null;
    };

    public Function<UserDto, String> saveOauthUser = userDto -> {
        if (userRepository.existsByUsername(userDto.getUsername())){
            UserDetails userDetails = userService.loadUserByUsername(userDto.getUsername());
            return utils.createJwt.apply(userDetails);
        }
        else{
            User user = new ObjectMapper().convertValue(userDto, User.class);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setUserRole(Role.ROLE_USER);
            user = userRepository.save(user);
            return utils.createJwt.apply(userService.loadUserByUsername(user.getUsername()));
        }
    };

    public String googleOauthUserJWT(String token){
        UserDto user =  getUserFromIdToken.apply(token);
        return saveOauthUser.apply(user);
    }
}
