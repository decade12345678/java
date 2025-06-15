package org.xzx.utils;

import cn.hutool.json.JSONUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xzx.constant.ExceptionConstant;
import org.xzx.entity.dto.MessageDTO;
import org.xzx.exception.BaseException;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChannelContextUtil {
    @Resource
    private RedisUtil redisUtil;
    private static final Logger logger = LoggerFactory.getLogger(ChannelContextUtil.class);
    //维护全局key
    private static final AttributeKey<Long> USER_ID_KEY = AttributeKey.newInstance("userId");
    //维护一个全局的用户与channel的哈希表映射，方便即拿即用
    private static final ConcurrentHashMap<Long, Channel> userChannelMap = new ConcurrentHashMap<>();
    public void addContext(Long user_id , Channel channel)
    {
        channel.attr(USER_ID_KEY).set(user_id);
        // 维护用户ID到Channel的映射
        userChannelMap.put(user_id, channel);
        logger.info("绑定关系:channel{}->user_id:{}",channel.id(),user_id);

    }

    public Long getUserId(Channel channel)
    {
        return channel.attr(USER_ID_KEY).get(); // 返回该Channel绑定的用户ID
    }
   //断开连接之前移除映射
    public void removeContextByChannel(Channel channel) {
        Long user_id = channel.attr(USER_ID_KEY).get();
        if (user_id != null) {
            channel.attr(USER_ID_KEY).set(null);
            userChannelMap.remove(user_id);  //调close方法前，先清空channel绑定的userid，并清理维护的映射表里的channel:userid信息
        }
    }

    public void MessageSend(MessageDTO messageDTO)
    {
        redisUtil.saveChatMessage(messageDTO);//保存消息记录到ID
        String JsonStr = JSONUtil.toJsonStr(messageDTO);
        //把消息给自己发一遍,因为不知道怎么处理，让自己显示
        userChannelMap.get(messageDTO.getSend_id()).writeAndFlush(new TextWebSocketFrame(JsonStr));
        Channel channel_to_friend = userChannelMap.get(messageDTO.getHear_id());
        //如果映射表里没有这个id对应的channel
        if (channel_to_friend==null ) {
                 throw new BaseException(ExceptionConstant.NOT_ONLINE_HAND);
        }
        else  userChannelMap.get(messageDTO.getHear_id()).writeAndFlush(new TextWebSocketFrame(JsonStr));

    }
}

