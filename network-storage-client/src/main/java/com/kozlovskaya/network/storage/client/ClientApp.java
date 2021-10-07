package com.kozlovskaya.network.storage.client;

import com.kozlovskaya.network.storage.common.Constants;
import com.kozlovskaya.network.storage.common.messages.file.FileRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientApp {


    public void run() throws InterruptedException {

        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap client = new Bootstrap();
            client.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            nioSocketChannel.pipeline().addLast(
                                    new ObjectEncoder(),// разбирает объект на байтбуфы и отправляет
                                    new ObjectDecoder(100 * 1024 * 1024, ClassResolvers.cacheDisabled(null)), // принимает байтбуфы и собирает в объект
                                    new ClientDecoder()
                            );
                        }
                    });

            ChannelFuture future = client.connect(Constants.HOST, Constants.PORT).sync();
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
                            System.out.println("Создан FileRequest содержащий данные файла: " + fileRequest.getFilename());
                            future.channel().writeAndFlush(fileRequest).sync();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new ClientApp().run();
    }
}
