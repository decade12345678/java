package org.xzx.entity.vo;

import lombok.Data;

@Data
public class UserVO {
    private Long id;
    private String user_name;
    private String chat_name;
    private String image;
    private int sex;
    private int age;
    private String describe;
    private int status;
}
