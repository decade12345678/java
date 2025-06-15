package org.xzx.entity.dto;

import lombok.Data;

@Data
public class FriendRequestDTO {
    private String chat_name;
    private String user_name;
    private String image;
    private String requested_user_name;
    private String message;
}
