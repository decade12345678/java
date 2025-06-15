package org.xzx.entity.pojo;

import lombok.Data;

@Data
public class Message {
    private Long send_id;
    private String message_content;
    private String message_type;
    private String file_size;
    private String file_name;
    private String file_type;
    private Long hear_id;
}
