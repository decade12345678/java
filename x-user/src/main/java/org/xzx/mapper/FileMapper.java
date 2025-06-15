package org.xzx.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.xzx.entity.dto.MessageDTO;
@Mapper
public interface FileMapper {


    @Insert("insert into message (send_id, hear_id, message_content, file_is, send_image, send_name, file_name, file_size, file_type, file_url, send_time) " +
            "values (#{send_id}, #{hear_id}, #{message_content}, #{file_is}, #{send_image}, #{send_name}, #{file_name}, #{file_size}, #{file_type}, #{file_url}, #{send_time})")
    void save(MessageDTO messageDTO);
}

