package com.kozlovskaya.network.storage.client.handlers;

import com.kozlovskaya.network.storage.common.messages.file.FileResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.RandomAccessFile;
import java.nio.file.Paths;

//считывает данные файла, который прислал сервер в ответ на запрос клиента
public class FileHandler extends SimpleChannelInboundHandler<FileResponse> {
    private static String clientStorage = "C:\\Users\\Elena\\IdeaProjects\\network-storage\\client_storage";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FileResponse message) throws Exception {
        System.out.println("Получен файл на клиенте");

        /*if (message instanceof FileResponse) {
            FileResponse fileResponse = (FileResponse) message;*/
            long fileSize = message.getFileSize();
            String fileName = (Paths.get(clientStorage, message.getFileName())).toString();
            try (RandomAccessFile accessFile = new RandomAccessFile(fileName, "rw")) {
                accessFile.seek(message.getFilePosition());
                accessFile.write(message.getFileData());
            }
        }
 /*   }*/

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
