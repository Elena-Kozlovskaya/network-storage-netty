package com.kozlovskaya.network.storage.client.handlers;

import com.kozlovskaya.network.storage.common.messages.AbstractMessage;
import com.kozlovskaya.network.storage.common.messages.file.FileResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.RandomAccessFile;
import java.nio.file.Paths;

//считывает данные файла, который прислал сервер в ответ на запрос клиента
public class ClientFileHandler extends SimpleChannelInboundHandler<AbstractMessage> {
    private static String clientStorage = "C:\\Users\\Elena\\IdeaProjects\\network-storage\\client_storage";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage message) throws Exception {
        System.out.println("Получен файл на клиенте");

        if (message instanceof FileResponse) {
            FileResponse fileResponse = (FileResponse) message;
            long fileSize = fileResponse.getFileSize();
            String fileName = (Paths.get(clientStorage, fileResponse.getFileName())).toString();
            try (RandomAccessFile accessFile = new RandomAccessFile(fileName, "rw")) {
                accessFile.seek(fileResponse.getFilePosition());
                accessFile.write(fileResponse.getFileData());
            }
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
