package org.xzx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.xzx.websocket.netty.NettyApplication;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.IOException;

@EnableTransactionManagement
@SpringBootApplication
public class XchatApplication implements CommandLineRunner {

    @Resource
    private NettyApplication nettyApplication;

    public static void main(String[] args) {
        SpringApplication.run(XchatApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // 启动Netty
        new Thread(nettyApplication).start();
    }


}