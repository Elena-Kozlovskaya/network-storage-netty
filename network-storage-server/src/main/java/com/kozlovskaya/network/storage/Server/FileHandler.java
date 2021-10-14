package com.kozlovskaya.network.storage.Server;

import com.kozlovskaya.network.storage.common.Constants;
import com.kozlovskaya.network.storage.common.messages.file.FileResponse;
import com.kozlovskaya.network.storage.common.messages.service.ServiceRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHandler extends SimpleChannelInboundHandler<ServiceRequest> {

    private static String serverStorage = "C:\\Users\\Elena\\IdeaProjects\\network-storage\\server_storage";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServiceRequest message) throws Exception {
        System.out.println("FileHandler на сервере запущен");
        /*if (message instanceof ServiceRequest) {
            ServiceRequest serviceRequest = (ServiceRequest) message;*/

            if (message.getCommand().equals(Constants.UPLOAD_FILE)) {
                /*Path path = Paths.get(serviceRequest.getFileName());
                String fileName = path.getFileName().toString();
                String directory = path.getParent().toString();*/
                String fileName = Paths.get(serverStorage, message.getFileName()).toString();
                Path path = Paths.get(serverStorage, message.getFileName());

                if (Files.exists(path)) {
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
                                break;
                            } else {
                                fileResponse.setFileSize(Files.size(path));
                                fileResponse.setFileData(buffer);
                                ctx.writeAndFlush(fileResponse);
                            }
                            buffer = new byte[1024 * 512];
                        }
                    }
                } else {
                    System.out.println("Нет такого файла");
                    //если нет такого файла отправить сообщ об ошибке
                }
            }
        }
    /*}*/

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
