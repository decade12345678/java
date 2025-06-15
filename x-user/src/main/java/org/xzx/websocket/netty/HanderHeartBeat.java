package org.xzx.websocket.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.xzx.utils.ChannelContextUtil;

import javax.annotation.Resource;


@Component
@ChannelHandler.Sharable
public class HanderHeartBeat extends ChannelDuplexHandler {
    @Resource
    private ChannelContextUtil channelContextUtil;
    private static final Logger logger =  LoggerFactory.getLogger(HanderHeartBeat.class);
           @Override
           public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
               if (evt instanceof IdleStateEvent) {
                   IdleStateEvent event = (IdleStateEvent) evt;
                   if(event.state()== IdleState.READER_IDLE) {
                       Channel channel = ctx.channel();
                       Long user_id = channelContextUtil.getUserId(channel);
                       logger.info("用户{} 心跳超时，关闭连接", user_id);
                       ctx.close();
                   }

               }
           }
}
