package org.xzx.entity.dto;

import lombok.Data;

@Data
public class FriendHandleDTO {
    private String user_name;
    private String requested_user_name;
    private int status;
}
