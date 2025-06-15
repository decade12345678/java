package org.xzx.service.impl;

import org.springframework.stereotype.Service;
import org.xzx.entity.dto.MessageDTO;
import org.xzx.mapper.FileMapper;
import org.xzx.service.FileService;
import org.xzx.utils.ChannelContextUtil;
import org.xzx.utils.FileType;

import javax.annotation.Resource;
import java.nio.file.Path;

@Service
public class FileServiceimpl implements FileService {
    @Resource
    private ChannelContextUtil channelContextUtil;
    @Resource
    private FileMapper fileMapper;
    @Override
    public void FileSend(Long sendId, Long hearId, String fileName, String fileSize, String fileType, String preview_url,String send_image,String send_name ){
        String message_content = FileType.getDisplayName(fileType);
        MessageDTO messageDTO = MessageDTO.builder().
                           send_id(sendId).
                           hear_id(hearId).
                           message_content(message_content).
                           file_is(1).
                           send_image(send_image).
                           send_name(send_name).
                           file_name(fileName).
                           file_size(fileSize).
                           file_type(fileType).
                           file_url(preview_url).
                           send_time(System.currentTimeMillis()).
                           build();
        //2.保存文件记录
        fileMapper.save(messageDTO);
        //3.调工具转发文件消息
        channelContextUtil.MessageSend(messageDTO);
    }
}
