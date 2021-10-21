package com.kozlovskaya.network.storage.client.handlers;

import com.kozlovskaya.network.storage.common.Constants;
import com.kozlovskaya.network.storage.common.messages.Request;
import com.kozlovskaya.network.storage.common.messages.file.FileMessage;
import com.kozlovskaya.network.storage.common.messages.file.FileRequest;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewClientSender {
    public static void sendFile(Path path, String command, Channel channel, ChannelFuture channelFuture) throws IOException, InterruptedException {
        System.out.println("NewClientSender sendFile() " + path + " ");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        if (command.equals(Constants.DOWNLOAD_FILE)) {
            if (Files.exists(path) & Files.isRegularFile(path)) {
                Path fileName = path.getFileName();
                Path parentName = path.getParent();
                System.out.println(fileName);
                executorService.execute(() -> {
                    File file = new File(parentName.toString(), fileName.toString());
                        System.out.println(file.length());
                    int bufSize = 1024 * 100;
                    int partsCount = (int) (file.length() / bufSize);
                    System.out.println("partsCount = " + partsCount);
                    if (file.length() % bufSize != 0) {
                        partsCount++;
                    }
                    FileMessage fileMessage = new FileMessage(fileName.toString(), -1, partsCount, new byte[bufSize]);
                    try (FileInputStream in = new FileInputStream(file)) {
                        for (int i = 0; i < partsCount; i++) {
                            int readBytes = in.read(fileMessage.getFileData());
                            System.out.println("readBytes = " + readBytes);
                            fileMessage.setPartNumber(i + 1);
                            System.out.println("PartNumber = " + fileMessage.getPartNumber());
                            if (readBytes < bufSize) {
                                fileMessage.setFileData(Arrays.copyOfRange(fileMessage.getFileData(), 0, readBytes));
                            }
                            fileMessage.setCommand(command);
                            channelFuture.channel().writeAndFlush(fileMessage).sync();
                            System.out.println("Отправлена часть #" + (i + 1));
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}