package com.example.tyfashionblogapplication.controller;

import com.example.tyfashionblogapplication.DTO.UserDto;
import com.example.tyfashionblogapplication.model.User;
import com.example.tyfashionblogapplication.serviceImpl.UserServiceImpl;
import com.example.tyfashionblogapplication.utils.GoggleJwtUtils;
import com.example.tyfashionblogapplication.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private UserServiceImpl userService;
    private PasswordEncoder passwordEncoder;
    private JwtUtils jwtUtils;
    private GoggleJwtUtils goggleJwtUtils;

    @Autowired
    public UserController(@Lazy UserServiceImpl userService, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, GoggleJwtUtils goggleJwtUtils) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.goggleJwtUtils = goggleJwtUtils;
    }

    @PostMapping("/user/sign-up")
    public ResponseEntity<UserDto> userSignUp(@RequestBody UserDto userDto) {
        User user = userService.saveUser(userDto);
        UserDto userDto1 = new ObjectMapper().convertValue(user, UserDto.class);
        return new ResponseEntity<>(userDto1, HttpStatus.CREATED);
    }

    @PostMapping("/admin/sign-up")
    public ResponseEntity<UserDto> adminSignUp(@RequestBody UserDto userDto) {
        User user = userService.saveAdmin(userDto);
        UserDto userDto1 = new ObjectMapper().convertValue(user, UserDto.class);
        return new ResponseEntity<>(userDto1, HttpStatus.CREATED);
    }

    @PostMapping("/user/login")
    public ResponseEntity<String> loginUser(@RequestBody UserDto userDto) {
        UserDetails user = userService.loadUserByUsername(userDto.getUsername());
        if (passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            String jwtToken = jwtUtils.createJwt.apply(user);
            return new ResponseEntity<>(jwtToken, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Username and Password incorrect", HttpStatus.BAD_REQUEST);
        }
    }
//    @PostMapping("/admin/login")
//    public ResponseEntity<String> loginAdmin(@RequestBody UserDto userDto){
//        UserDetails admin = userService.loadUserByUsername(userDto.getUsername());
//        if (passwordEncoder.matches(userDto.getPassword(), admin.getPassword())){
//            String jwtToken = jwtUtils.createJwt.apply(admin);
//            return new ResponseEntity<>(jwtToken, HttpStatus.CREATED);
//        }else {
//            return new ResponseEntity<>("Username and Password incorrect", HttpStatus.BAD_REQUEST);
//        }
//    }

    @GetMapping("/google/{tok}")
    public ResponseEntity<String> googleAuth(@PathVariable("tok") String token) {
        String newToken = goggleJwtUtils.googleOauthUserJWT(token);
        return new ResponseEntity<String>(newToken, HttpStatus.OK);

//    @SecurityRequirement(name = "Bearer Authentication")
//    @GetMapping("/dashboard")
//    public String dashboard(){
//        return "Welcome to your Dashboard";
//    }
    }
}
