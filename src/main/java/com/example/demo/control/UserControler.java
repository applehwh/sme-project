package com.example.demo.control;

import com.example.demo.entity.UserEntity;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("user")
public class UserControler {
    @Autowired
    private UserService userService;

    @GetMapping
    public Flux<UserEntity>getUser(UserEntity entity){
        return userService.getUser(entity);
    }

    @PostMapping
    public Mono<UserEntity> signUpUser(UserEntity entity){
        return userService.signUpUser(entity);
    }
}
