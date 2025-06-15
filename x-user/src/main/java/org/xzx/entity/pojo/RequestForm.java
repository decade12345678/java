package org.xzx.entity.pojo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Builder
@Data
public class RequestForm {
    private String chat_name;
    private String user_name;
    private String image;
    private String requested_user_name;
    private LocalDateTime request_time;
    private String request_message;
    private int status;
}
