package org.xzx.entity.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileMessageDTO {
    private Long sendId;
    private Long hearId;
    private String file_name;
    private String file_type;
    private String file_size;
    private MultipartFile file;
}