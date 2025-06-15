package org.xzx.entity.vo;

import lombok.Data;

@Data
public class FriendSearchVO {
    private String user_name;//好友的用户名
    private String chat_name;
    private String image;
    private int status;//查询是否为自己好友
}
