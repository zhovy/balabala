package com.love.chat;

import com.love.chat.core.BaseServer;
import com.love.chat.handler.*;
import com.love.chat.util.Constants;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author laochunyu
 * @site http://www.wolfbe.com
 * @github https://github.com/beyondfengyu
 */
public class HappyChatServer extends BaseServer {
    private final ScheduledExecutorService executorService;

    public HappyChatServer(int port) {
        this.port = port;
        executorService = Executors.newScheduledThreadPool(2);
    }

    @Override
    public void start() {
        b.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .localAddress(new InetSocketAddress(port))
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(defLoopGroup,
                                new HttpServerCodec(),   //请求解码器
                                new HttpObjectAggregator(65536),//将多个消息转换成单一的消息对象
                                new ChunkedWriteHandler(),  //支持异步发送大的码流，一般用于发送文件流
                                new IdleStateHandler(60 * 60, 0, 0), //检测链路是否读空闲
                                new DispatchHandler(), // 请求分发
                                new UserAuthHandler(), //处理握手和认证
                                new MessageHandler(),    //处理消息的发送
                                new FileUploadServerHandler()    //文件上传服务
                        );
                    }
                });

        try {
            cf = b.bind().sync();
            InetSocketAddress addr = (InetSocketAddress) cf.channel().localAddress();
            logger.info("WebSocketServer start success, port is:{}", addr.getPort());

            // 定时扫描所有的Channel，关闭失效的Channel
            executorService.scheduleAtFixedRate(() -> {
                logger.info("scanNotActiveChannel --------");
                UserInfoManager.scanNotActiveChannel();
            }, 3, 60 * 60, TimeUnit.SECONDS);

            // 定时向所有客户端发送Ping消息
            executorService.scheduleAtFixedRate(UserInfoManager::broadCastPing, 3, 50, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            logger.error("WebSocketServer start fail,", e);
        }
        final File uploadDir = new File(Constants.FILE_UPLOAD_ABS_PATH_PREFIX);
        if (!uploadDir.exists()) {

            logger.info("HappyServer file uploadDir create:"+ uploadDir.getAbsolutePath());
        }
    }

    @Override
    public void shutdown() {
        if (executorService != null) {
            executorService.shutdown();
        }
        super.shutdown();
    }
}
