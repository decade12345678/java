package org.xzx.websocket.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
public class NettyApplication  implements Runnable{
    //定义服务器线程组和工作线程组
    private static final Logger logger = LoggerFactory.getLogger(NettyApplication.class);
    private static EventLoopGroup bossgroup = new NioEventLoopGroup(1);
    private static EventLoopGroup workgroup = new NioEventLoopGroup();
    @Resource
    private HanderWebsocket handerWebsocket;
    @Resource
    private HanderHeartBeat handerHeartBeat;

    @Override
    public void run() {
        try{
            //定义服务器启动类，将两个线程组加入启动类
            ServerBootstrap serverbootstrap = new ServerBootstrap();
            serverbootstrap.group(bossgroup, workgroup);
            //设置通道为NIOchannel,服务器hander日志级别为debug，childhander即为字处理器，即处理用户连接的socketchannel
            serverbootstrap.channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG)).childHandler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel channel)  {
                            ChannelPipeline pipeline =channel.pipeline();
                            pipeline.addLast(new HttpServerCodec());//http编码
                            pipeline.addLast(new HttpObjectAggregator(64*1024));
                            pipeline.addLast(new IdleStateHandler(6,0,0, TimeUnit.SECONDS));
                            pipeline.addLast(handerHeartBeat);//重写的心跳检测
                            pipeline.addLast(new WebSocketServerProtocolHandler("/ws",null,true,65536,true,true,10000L));//升级为ws协议，后缀要加ws才能连接
                            pipeline.addLast(handerWebsocket);//重写的监听连接
                        }
                    });
            ChannelFuture channelfuture = serverbootstrap.bind(5050).sync();
            logger.info("Netty启动类启动成功");
            channelfuture.channel().closeFuture().sync();


        }catch (Exception e)
        {
            logger.info("Netty启动类启动失败");
        }finally {
            bossgroup.shutdownGracefully();
            workgroup.shutdownGracefully();
        }
    }
}
