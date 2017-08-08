package com.steven.gateway;


import com.steven.gateway.handler.RedisCommandDecoder;
import com.steven.gateway.handler.RedisCommandHandler;
import com.steven.gateway.handler.RedisReplyEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by cc on 2017/4/25.
 */
public class Gateway {
    private Logger logger = LogManager.getLogger(Gateway.class);
    private int localPort = 5555;

    public void run() {
        logger.info("gateway start... listening port {}", localPort);
        EventLoopGroup bossGroup = new NioEventLoopGroup(8);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            ch.pipeline().addLast(new RedisCommandDecoder())
                                    .addLast(new RedisReplyEncoder())
                                    .addLast(new RedisCommandHandler());
                        }
                    });

            ChannelFuture f = b.bind(localPort).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("exception occur, system will exit. {}", e.getMessage());
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
