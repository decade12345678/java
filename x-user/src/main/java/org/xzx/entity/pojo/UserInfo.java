package org.xzx.entity.pojo;

import lombok.Data;

@Data
public class UserInfo {
    private Long id;
    private String user_name;
    private String pass_word;
    private String chat_name;
    private String image;
    private String phone;
}
