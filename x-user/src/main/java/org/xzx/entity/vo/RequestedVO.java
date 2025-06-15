package org.xzx.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class RequestedVO {
    private String chat_name;
    private String user_name;
    private String image;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime request_time;
    private String request_message;
    private int status;
}
