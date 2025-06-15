package org.xzx.service.impl;

import org.springframework.stereotype.Service;
import org.xzx.entity.dto.MessageDTO;
import org.xzx.service.MessageService;
import org.xzx.utils.ChannelContextUtil;

import javax.annotation.Resource;

@Service
public class MessageServiceimpl implements MessageService {
    @Resource
    private ChannelContextUtil channelContextUtil;
    @Override
    public void TextSend(MessageDTO messageDTO) {
        messageDTO.setSend_time(System.currentTimeMillis());
        messageDTO.setFile_is(0);
        channelContextUtil.MessageSend(messageDTO);
    }
}
