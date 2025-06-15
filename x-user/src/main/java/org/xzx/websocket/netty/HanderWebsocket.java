package org.xzx.websocket.netty;

import io.jsonwebtoken.Claims;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xzx.mapper.UserMapper;
import org.xzx.properties.JwtProperties;

import org.xzx.utils.ChannelContextUtil;
import org.xzx.utils.JwtUtil;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;


@Component
@ChannelHandler.Sharable
public class HanderWebsocket extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final Logger logger = LoggerFactory.getLogger(HanderWebsocket.class);
    @Resource
    private JwtProperties jwtproperties;
    @Resource
    private ChannelContextUtil channelContextUtil;
    @Resource
    private UserMapper userMapper;


    //通道就绪后调用，用户用来初始化
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("有新连接接入");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        Long user_id = channelContextUtil.getUserId(channel);
        channelContextUtil.removeContextByChannel(channel);
        userMapper.updateStatus(user_id,0);
        logger.info("用户{}连接断开",user_id);

    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        Channel channel = ctx.channel();
        Long user_id = channelContextUtil.getUserId(channel);
        logger.info("收到用户{}的心跳检测:{}",user_id,msg.text());
    }

    //这里是处理请求，解析token出用户id，然后用工具类把id与channel绑定
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            WebSocketServerProtocolHandler.HandshakeComplete complete = (WebSocketServerProtocolHandler.HandshakeComplete) evt;
            String url =complete.requestUri();
            String token = getToken(url);
            if(token == null) {
                ctx.close();
                return;
            }
            //解析token，绑定用户和channel
            Claims claims = JwtUtil.parseJWT(jwtproperties.getSecretKey(), token);
            Long userId = Long.valueOf(claims.get("userId").toString());
            channelContextUtil.addContext(userId,ctx.channel());
            userMapper.updateStatus(userId,1);
        }
    }

    private String getToken(String url) {
       //请求为/ws?token=xxxx首先判断是否为空或者有没有？查询参数
        if(url==null || !url.contains("?")){
            return null;
        }
        //用问号分割得到ws和token后面的，如果这里不是2，说明不符合格式
        String []query1 = url.split("\\?");
        if(query1.length!=2){
            return null;
        }
        //再用等于号分割，如果没有token后面的值说明也是空
        String []query2 = query1[1].split("=");
        if(query2.length!=2){
            return null;
        }
        return query2[1];
    }
}
