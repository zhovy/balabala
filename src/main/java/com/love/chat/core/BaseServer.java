package com.love.chat.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


public abstract class BaseServer implements Server {

    protected Logger logger = LoggerFactory.getLogger(BaseServer.class);
    protected String host = "192.168.10.76";
    protected int port = 8099;

    protected DefaultEventLoopGroup defLoopGroup;
    protected NioEventLoopGroup bossGroup;
    protected NioEventLoopGroup workGroup;
    protected NioServerSocketChannel socketChannel;
    protected ChannelFuture channelFuture;
    protected ServerBootstrap bootstrap;

    public void init() throws InterruptedException {
        defLoopGroup = new DefaultEventLoopGroup(8, new ThreadFactory() {
            private final AtomicInteger index = new AtomicInteger(0);

            @Override
            public Thread newThread(@NotNull Runnable r) {
                return new Thread(r, "DEFAULTEVENTLOOPGROUP_" + index.incrementAndGet());
            }
        });
        bossGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
            private final AtomicInteger index = new AtomicInteger(0);

            @Override
            public Thread newThread(@NotNull Runnable r) {
                return new Thread(r, "BOSS_" + index.incrementAndGet());
            }
        });
        workGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 10, new ThreadFactory() {
            private final AtomicInteger index = new AtomicInteger(0);

            @Override
            public Thread newThread(@NotNull Runnable r) {
                return new Thread(r, "WORK_" + index.incrementAndGet());
            }
        });
        socketChannel = new NioServerSocketChannel();

        bootstrap.childOption(ChannelOption.TCP_NODELAY,Boolean.TRUE);
        bootstrap = new ServerBootstrap();
        //绑定端口后，开启监听
        ChannelFuture future = bootstrap.bind(port).sync();
        future.addListener(f -> {
            if (f.isSuccess()) {
                System.out.println("服务启动成功");
            } else {
                System.out.println("服务启动失败");
            }
        });
        //等待服务监听端口关闭
        future.channel().closeFuture().sync();
    }

    @Override
    public void shutdown() {
        if (defLoopGroup != null) {
            defLoopGroup.shutdownGracefully();
        }
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }

}
