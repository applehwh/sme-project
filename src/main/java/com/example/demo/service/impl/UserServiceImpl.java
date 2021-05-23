package com.example.demo.service.impl;

import com.example.demo.entity.UserEntity;
import com.example.demo.service.UserService;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class UserServiceImpl implements UserService {
    @Autowired
    ReactiveMongoTemplate mongoTemplate;

    @Override
    public Flux<UserEntity> getUser(UserEntity entiy) {
        return  mongoTemplate.find(new Query(Criteria.where("sex").is("0")),UserEntity.class);
    }

    @Override
    public Mono<UserEntity> signUpUser(UserEntity entity) {
        return mongoTemplate.insert(entity);
    }
}
