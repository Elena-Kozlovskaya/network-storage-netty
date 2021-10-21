package com.kozlovskaya.network.storage.Server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

public class Server {
    public static void main(String[] args) throws InterruptedException {
        new Server().run();
    }

    public void run() throws InterruptedException {

        NioEventLoopGroup boosGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    .group(boosGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline().addLast(
                                    new LengthFieldBasedFrameDecoder(
                                            1024 * 1024 * 1024,
                                            0,
                                            8,
                                            0,
                                            8
                                    ),
                                    new LengthFieldPrepender(8),
                                    new ByteArrayDecoder(),
                                    new ByteArrayEncoder(),
                                    new JsonDecoder(),
                                    new JsonEncoder(),
                                    new NewServerHandler()/*,
                                    new ServerFileHandler()*/
                            );
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture channelFuture = serverBootstrap.bind(9000).sync();
            System.out.println("Server started");
            channelFuture.channel().closeFuture().sync();
        } finally {
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
