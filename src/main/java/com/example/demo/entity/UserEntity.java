package com.example.demo.entity;

import com.example.demo.Constant.TableNameConstant;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = TableNameConstant.USER)
public class UserEntity {

    private String userName;
    private String passWord;
    private String sex;
}
