package org.xzx.entity.pojo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Builder
public class FriendForm {
    private Long user_id;
    private Long friend_id;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private int status;
    private int chat_method;
}
