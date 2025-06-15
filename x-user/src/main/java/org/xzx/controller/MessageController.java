package org.xzx.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.xzx.Result.Result;
import org.xzx.entity.dto.MessageDTO;
import org.xzx.service.MessageService;


import javax.annotation.Resource;

@Api(tags="文本消息相关管理接口")
@RestController
@RequestMapping("/message")
public class MessageController {
        @Resource
        private MessageService messageService;
        @ApiOperation("发送文本消息")
        @PostMapping("/TextSend")
        public Result textsend(@RequestBody MessageDTO textDTO) {
                messageService.TextSend(textDTO);
                return Result.success();
        }

    
}
