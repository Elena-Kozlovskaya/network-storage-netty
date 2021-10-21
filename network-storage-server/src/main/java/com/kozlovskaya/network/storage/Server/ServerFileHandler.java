package com.kozlovskaya.network.storage.Server;

import com.kozlovskaya.network.storage.common.Constants;
import com.kozlovskaya.network.storage.common.messages.Request;
import com.kozlovskaya.network.storage.common.messages.file.FileResponse;
import com.kozlovskaya.network.storage.common.messages.service.ServiceResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerFileHandler extends SimpleChannelInboundHandler<Request> {

    private static String serverStorage = "C:\\Users\\Elena\\IdeaProjects\\network-storage\\server_storage";
    private ServiceResponse serviceResponse = new ServiceResponse();
    private ExecutorService executorService;

    public ServerFileHandler() {
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request message) {
        System.out.println("FileHandler на сервере запущен");

        if (message.getCommand().equals(Constants.UPLOAD_FILE)) {
            String fileName = Paths.get(serverStorage, message.getFileName()).toString();
            Path path = Paths.get(serverStorage, message.getFileName());
            if (Files.exists(path)) {

           /*    executorService.execute(() ->{*/
                   byte[] buffer = new byte[1024 * 512];
                try (RandomAccessFile accessFile = new RandomAccessFile(fileName, "r")) {
                    while (true) {
                        FileResponse fileResponse = new FileResponse();
                        fileResponse.setFileName(message.getFileName());
                        fileResponse.setFilePosition(accessFile.getFilePointer());
                        int read = accessFile.read(buffer);
                        if (read < buffer.length - 1) {
                            byte[] tempBuffer = new byte[read];
                            System.arraycopy(buffer, 0, tempBuffer, 0, read);
                            fileResponse.setFileData(tempBuffer);
                            fileResponse.setFileSize(Files.size(path));
                            ctx.writeAndFlush(fileResponse);
                            System.out.println("FileHandler fileResponse " + fileResponse.getFileSize());
                            break;
                        } else {
                            fileResponse.setFileSize(Files.size(path));
                            fileResponse.setFileData(buffer);
                            ctx.writeAndFlush(fileResponse);
                        }
                        buffer = new byte[1024 * 512];
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            /*});*/
            } else {
                System.out.println("Нет такого файла");
                //если нет такого файла отправить сообщ об ошибке
            }
        }

        if (message.getCommand().equals(Constants.DOWNLOAD_FILE)) {
            System.out.println("DOWNLOAD_FILE");
            String fileName = (Paths.get(serverStorage, message.getFileName())).toString();
            System.out.println(fileName);
            if (!Files.exists(Paths.get(fileName))) {
                long fileSize = message.getFileSize();
                try (RandomAccessFile accessFile = new RandomAccessFile(fileName, "rw")) {
                    accessFile.seek(message.getFilePosition());
                    accessFile.write(message.getFileData());
                    System.out.println("File " + message.getFileName() + " created in " + serverStorage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
                   /* this.serviceResponse.setResponse("File " + fileRequest.getFileName() + " created in " + serverStorage);
                    ctx.writeAndFlush(serviceResponse);*/
            else {
                System.out.println("File " + message.getFileName() + " is already exists in " + serverStorage);
                    /*
                    this.serviceResponse.setResponse("File " + fileRequest.getFileName() + " is already exists in " + serverStorage);
                    ctx.writeAndFlush(serviceResponse);*/
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
