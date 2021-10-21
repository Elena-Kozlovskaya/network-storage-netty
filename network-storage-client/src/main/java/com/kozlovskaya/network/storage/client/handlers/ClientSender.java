package com.kozlovskaya.network.storage.client.handlers;

import com.kozlovskaya.network.storage.common.Constants;
import com.kozlovskaya.network.storage.common.messages.Request;
import com.kozlovskaya.network.storage.common.messages.file.FileRequest;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientSender {

    public static void sendFile(Path path, String command, Channel channel, ChannelFuture channelFuture) {
        System.out.println("FileSender sendFile() " + path + " ");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        if (command.equals(Constants.DOWNLOAD_FILE)) {
            if (Files.exists(path) & Files.isRegularFile(path)) {
                String fileName = (Paths.get(path.getFileName().toString())).toString();
                /*executorService.execute(() -> {*/
                    byte[] buffer = new byte[1024 * 512];
                    try (RandomAccessFile accessFile = new RandomAccessFile(path.toString(), "r")) {
                        while (true) {
                            FileRequest fileRequest = new FileRequest();
                            fileRequest.setFileName(fileName);
                            fileRequest.setFilePosition(accessFile.getFilePointer());
                            int read = accessFile.read(buffer);
                            System.out.println("read = " + read);
                            System.out.println(" buffer.length = " + (buffer.length));
                            if (read == buffer.length) {
                                System.out.println("здесь");
                                byte[] tempBuffer = new byte[read];
                                System.arraycopy(buffer, 0, tempBuffer, 0, read);
                                fileRequest.setCommand(command);
                                fileRequest.setFileData(tempBuffer);
                                fileRequest.setFileSize(tempBuffer.length);
                                System.out.println(fileRequest.getFileSize());
                                channelFuture.channel().writeAndFlush(fileRequest).sync();
                                /*if (!channelFuture.isSuccess()) {
                                    channelFuture.cause().printStackTrace();
                                }*/
                               // channelFuture.channel().closeFuture().sync();
                                System.out.println("FileHandler fileRequest " + fileRequest.getFileSize());
                                break;
                            } else {
                                System.out.println("вот здесь");
                                fileRequest.setFileSize(Files.size(path));
                                fileRequest.setFileData(buffer);
                                System.out.println(fileRequest.getFileSize());
                                channelFuture.channel().writeAndFlush(fileRequest).sync();
                                if (!channelFuture.isSuccess()) {
                                    channelFuture.cause().printStackTrace();
                                }
                                channelFuture.channel().closeFuture().sync();
                            }
                            buffer = new byte[1024 * 512];
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                /*});*/
            } else {
                System.out.println("Нет такого файла");
                //если нет такого файла отправить сообщ об ошибке
            }
        }
        if (command.equals(Constants.UPLOAD_FILE)) {
            Request request = new Request();
            request.setFileName(path.getFileName().toString());
            request.setCommand(command);
            System.out.println("Создан Request содержащий данные файла: " + request.getFileName());
            try {
                channelFuture.channel().writeAndFlush(request).sync();
                if (!channelFuture.isSuccess()) {
                    channelFuture.cause().printStackTrace();
                }
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
