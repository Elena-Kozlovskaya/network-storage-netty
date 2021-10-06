package com.kozlovskaya.network.storage.Server;

import com.kozlovskaya.network.storage.common.AbstractMessage;
import com.kozlovskaya.network.storage.common.FileMessage;
import com.kozlovskaya.network.storage.common.FileRequest;
import com.kozlovskaya.network.storage.common.FileResponds;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ServerHandler extends SimpleChannelInboundHandler<AbstractMessage> {

    private static String serverStorage = "C:\\Users\\Elena\\IdeaProjects\\network-storage\\client_storage";

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
            if (Files.exists(Paths.get(serverStorage + fileRequest.getFilename()))) {
                FileMessage fileMessage = new FileMessage(Paths.get(serverStorage + fileRequest.getFilename()));
                ctx.writeAndFlush(fileMessage);
            }
        }

        if (message instanceof FileMessage) {
            FileMessage fileMessage = (FileMessage) message;
            if (!Files.exists(Paths.get(serverStorage + fileMessage.getFilename()))) {
                Files.write(Paths.get(serverStorage + fileMessage.getFilename()), fileMessage.getData(), StandardOpenOption.CREATE_NEW);
                ctx.writeAndFlush(new FileResponds("File created in " + serverStorage + fileMessage.getFilename()));
            } else {
                ctx.writeAndFlush(new FileResponds("File " + fileMessage.getFilename() + " is already exists in " + serverStorage));
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
