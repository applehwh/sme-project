package com.example.demo.control;

import com.example.demo.entity.UserEntity;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("user")
public class UserControler {
    @Autowired
    private UserService userService;

    public Flux<UserEntity>getUser(UserEntity entity){
        return userService.getUser(entity);
    }

    public Mono<UserEntity> signUpUser(UserEntity entity){
        return userService.signUpUser(entity);
    }
}
