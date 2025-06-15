package org.xzx.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
//纯文本消息前端给的，同时也用于传回前端
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Long send_id;
    private String send_image;
    private String send_name;

    private Long hear_id;

    private String message_type;
    private String message_content;


    private int file_is;
    private String file_name;
    private String file_type;
    private String file_size;
    private String file_url;

    private Long send_time;
}
