package com.love.chat;

import com.love.chat.server.ChatServer;
import com.love.chat.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ChatMain {
    private static final Logger logger = LoggerFactory.getLogger(ChatMain.class);

    // 聊天服务器
    public static void main(String[] args) throws InterruptedException {
        final ChatServer server = new ChatServer(Constants.DEFAULT_PORT);
        server.init();
        server.start();
        // 注册进程钩子，在JVM进程关闭前释放资源
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.shutdown();
            logger.warn(">>>>>>>>>> jvm shutdown");
            System.exit(0);
        }));
    }
}
