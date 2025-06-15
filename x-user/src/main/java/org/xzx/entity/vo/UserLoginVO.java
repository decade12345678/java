package org.xzx.entity.vo;

import lombok.Builder;
import lombok.Data;
import org.xzx.entity.dto.MessageDTO;

import java.util.List;
//注释也算改
@Builder
@Data
public class UserLoginVO {
    private Long id;
    private String user_name;
    private String image;
    private String chat_name;
    private String token;
    private List<MessageDTO> textDTOList;
}
