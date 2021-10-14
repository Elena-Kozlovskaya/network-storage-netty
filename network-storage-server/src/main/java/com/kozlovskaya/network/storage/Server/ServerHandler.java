package com.kozlovskaya.network.storage.Server;

import com.kozlovskaya.network.storage.common.*;
import com.kozlovskaya.network.storage.common.messages.AbstractMessage;
import com.kozlovskaya.network.storage.common.messages.file.FileMessage;
import com.kozlovskaya.network.storage.common.messages.file.FileRequest;
import com.kozlovskaya.network.storage.common.messages.service.ServiceResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ServerHandler extends SimpleChannelInboundHandler<AbstractMessage> {

    private static String serverStorage = "C:\\Users\\Elena\\IdeaProjects\\network-storage\\server_storage";
    private ServiceResponse serviceResponse = new ServiceResponse();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client disconnected");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage message) throws Exception {
        System.out.println("Получен файл");

        if (message instanceof FileRequest) {
            FileRequest fileRequest = (FileRequest) message;
            if (fileRequest.getCommand().equals(Constants.DOWNLOAD_FILE)) {
                if (!Files.exists(Paths.get(serverStorage + fileRequest.getFileName()))) {
                    Files.write(Paths.get(serverStorage, fileRequest.getFileName()), fileRequest.getFileData(), StandardOpenOption.CREATE_NEW);
                    this.serviceResponse.setResponse("File " + fileRequest.getFileName() + " created in " + serverStorage);
                    ctx.writeAndFlush(serviceResponse);
                } else {
                    ServiceResponse serviceResponse = new ServiceResponse();
                    this.serviceResponse.setResponse("File " + fileRequest.getFileName() + " is already exists in " + serverStorage);
                    ctx.writeAndFlush(serviceResponse);
                }
                FileMessage fileMessage = new FileMessage(Paths.get(serverStorage + fileRequest.getFileName()));
                ctx.writeAndFlush(fileMessage);
            } else {
                System.out.println("Command is wrong");
            }
        }

        if (message instanceof FileMessage) {
            FileMessage fileMessage = (FileMessage) message;
            if (!Files.exists(Paths.get(serverStorage + fileMessage.getFilename()))) {
                Files.write(Paths.get(serverStorage + fileMessage.getFilename()), fileMessage.getData(), StandardOpenOption.CREATE_NEW);
                this.serviceResponse.setResponse("File created in " + serverStorage + fileMessage.getFilename());
                ctx.writeAndFlush(serviceResponse);
            } else {
                this.serviceResponse.setResponse("File " + fileMessage.getFilename() + " is already exists in " + serverStorage);
                ctx.writeAndFlush(serviceResponse);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
