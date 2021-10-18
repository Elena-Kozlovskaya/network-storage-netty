package com.kozlovskaya.network.storage.client;

import com.kozlovskaya.network.storage.client.handlers.ClientFileHandler;
import com.kozlovskaya.network.storage.client.handlers.ClientSender;
import com.kozlovskaya.network.storage.client.handlers.JsonDecoder;
import com.kozlovskaya.network.storage.client.handlers.JsonEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Client {
    private Channel currentChannel;

    public static void main(String[] args) throws InterruptedException {
        new Client().start();
    }

    public Channel getCurrentChannel() {
        return currentChannel;
    }

    public void start() throws InterruptedException {

        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap client = new Bootstrap();
            client.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
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
                                    new ClientFileHandler()
                            );
                            currentChannel = ch;
                        }
                    });

            ChannelFuture channelFuture = client.connect("localhost", 9000).sync();

            System.out.println("Client started");
            //Временное решение вместо GUI
            while (true) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split("\\s+", 2);
                        System.out.println(parts.length);
                        String command = parts[0];
                        Path path = Paths.get(parts[1]);
                        System.out.println(command);
                        System.out.println(path);
                        ClientSender.sendFile(path, command, currentChannel, channelFuture);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            /*Request request = new Request();
            request.setFilename("test.txt");

            channelFuture.channel().writeAndFlush(request);
            channelFuture.channel().closeFuture().sync();*/
        } finally {
            group.shutdownGracefully();
        }
    }

    public void open() {

    }
}

