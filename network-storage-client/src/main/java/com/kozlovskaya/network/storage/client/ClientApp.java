package com.kozlovskaya.network.storage.client;

import com.kozlovskaya.network.storage.common.FileMessage;
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
                            //для каждого соединения открывается новый канал
                            // инициализируем канал, содержит массив сущностей для работы
                            //new StringEncoder(), new StringDecoder() - преобразование объектов в массив байт, важен порядок энкодеров и декодеров слева направо
                            nioSocketChannel.pipeline().addLast(
                                    new ObjectEncoder(),// разбирает объект на байтбуфы и отправляет
                                    new ObjectDecoder(100 * 1024 * 1024, ClassResolvers.cacheDisabled(null)), // принимает байтбуфы и собирает в объект
                                    new ClientDecoder()
                            );
                            //   new StringDecoder(),//преобразовали ByteBuf в String
                            // new ByteArrayEncoder(),//3.преобразовали массив байт в ByteBuf
                            // new LengthFieldPrepender(4)); //положение неверно - добавляет первые 4 байта для каждого сообщения, которые содержат размер сообщения
                            //   new MessageEncoder(),//2.преобразовали строку в массив байт
                            // new ClientDecoder());//1. (исх.сообщение) отправил строку
                        }
                    });

            ChannelFuture future = client.connect("localhost", 9000).sync();
            System.out.println("Client started");
            while (true) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        Path path = Paths.get(line);

                        if (Files.exists(path) & Files.isRegularFile(path)) {
                            FileMessage fileMessage = new FileMessage(path);
                            System.out.println("Создан файл с именем: " + fileMessage.getFilename());
                            future.channel().writeAndFlush(fileMessage).sync();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                /*while (true) {

                    // writeAndFlush - запиши в буффер и отправь на сервер
                    // заменить на write if(содержит знак конца меседжа) {flush()}
                    future.channel().writeAndFlush("Hello from client!").sync();

                    Thread.sleep(5000);
                }
            }*/
            }
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new ClientApp().run();
    }
}
