package com.kozlovskaya.network.storage.client;

import com.kozlovskaya.network.storage.client.handlers.FileHandler;
import com.kozlovskaya.network.storage.client.handlers.JsonDecoder;
import com.kozlovskaya.network.storage.client.handlers.JsonEncoder;
import com.kozlovskaya.network.storage.common.messages.Request;
import com.kozlovskaya.network.storage.common.messages.file.FileRequest;
import com.kozlovskaya.network.storage.common.messages.service.ServiceRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        new Client().start();
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
                                    new FileHandler()
                            );
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

                        if (Files.exists(path) & Files.isRegularFile(path)) {
                            FileRequest fileRequest = new FileRequest(path, command);
                            System.out.println("Создан FileRequest содержащий данные файла: " + fileRequest.getFileName());
                            channelFuture.channel().writeAndFlush(fileRequest).sync();
                            if (!channelFuture.isSuccess()){
                                channelFuture.cause().printStackTrace();
                            }
                            if(channelFuture.isSuccess()){
                                System.out.println("FileRequest отправлен на сервер");
                            }
                        } else {
                            ServiceRequest serviceRequest = new ServiceRequest();
                            serviceRequest.setCommand(command);
                            serviceRequest.setFileName(path.getFileName().toString());
                            System.out.println("Создан ServiceRequest c запросом для файла: " + serviceRequest.getFileName());
                            channelFuture.channel().writeAndFlush(serviceRequest).sync();
                            if (!channelFuture.isSuccess()){
                                channelFuture.cause().printStackTrace();
                            }
                            if(channelFuture.isSuccess()){
                                System.out.println("ServiceRequest доставлен на сервер");
                            }
                        }


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

    public void open(){

    }
}

