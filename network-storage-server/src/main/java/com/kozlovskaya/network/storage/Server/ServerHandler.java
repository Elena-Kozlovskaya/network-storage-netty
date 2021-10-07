package com.kozlovskaya.network.storage.Server;

import com.kozlovskaya.network.storage.common.*;
import com.kozlovskaya.network.storage.common.messages.AbstractMessage;
import com.kozlovskaya.network.storage.common.messages.file.FileMessage;
import com.kozlovskaya.network.storage.common.messages.file.FileRequest;
import com.kozlovskaya.network.storage.common.messages.service.Responds;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ServerHandler extends SimpleChannelInboundHandler<AbstractMessage> {

    private static String serverStorage = "C:\\Users\\Elena\\IdeaProjects\\network-storage\\server_storage";

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

            if (fileRequest.getCommand().equals(Constants.CREATE_FILE)) {
                if (!Files.exists(Paths.get(serverStorage + fileRequest.getFilename()))) {
                    Files.write(Paths.get(serverStorage, fileRequest.getFilename()), fileRequest.getFileData(), StandardOpenOption.CREATE_NEW);
                    ctx.writeAndFlush(new Responds("File " + fileRequest.getFilename() + " created in " + serverStorage));
                } else {
                    ctx.writeAndFlush(new Responds("File " + fileRequest.getFilename() + " is already exists in " + serverStorage));
                }
                FileMessage fileMessage = new FileMessage(Paths.get(serverStorage + fileRequest.getFilename()));
                ctx.writeAndFlush(fileMessage);
            } else {
                System.out.println("Command is wrong");
            }
        }

        if (message instanceof FileMessage) {
            FileMessage fileMessage = (FileMessage) message;
            if (!Files.exists(Paths.get(serverStorage + fileMessage.getFilename()))) {
                Files.write(Paths.get(serverStorage + fileMessage.getFilename()), fileMessage.getData(), StandardOpenOption.CREATE_NEW);
                ctx.writeAndFlush(new Responds("File created in " + serverStorage + fileMessage.getFilename()));
            } else {
                ctx.writeAndFlush(new Responds("File " + fileMessage.getFilename() + " is already exists in " + serverStorage));
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
