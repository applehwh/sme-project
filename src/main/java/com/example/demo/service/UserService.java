package com.example.demo.service;

import com.example.demo.entity.UserEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface UserService {
    public Flux<UserEntity> getUser(UserEntity entiy);
    public Mono<UserEntity> signUpUser(UserEntity entity);
}
