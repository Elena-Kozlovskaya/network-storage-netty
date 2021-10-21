package com.kozlovskaya.network.storage.Server;

import com.kozlovskaya.network.storage.common.Constants;
import com.kozlovskaya.network.storage.common.messages.Request;
import com.kozlovskaya.network.storage.common.messages.file.FileMessage;
import com.kozlovskaya.network.storage.common.messages.service.ServiceResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewServerHandler extends SimpleChannelInboundHandler <FileMessage> {
    private static String serverStorage = "C:\\Users\\Elena\\IdeaProjects\\network-storage\\server_storage";
    private ServiceResponse serviceResponse = new ServiceResponse();
    private ExecutorService executorService;
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public NewServerHandler() {
        System.out.println("NewServerHandler на сервере запущен");
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FileMessage fileMessage) throws Exception {
        if (fileMessage.getCommand().equals(Constants.DOWNLOAD_FILE)) {
            System.out.println("DOWNLOAD_FILE");
            String fileName = (Paths.get(serverStorage, fileMessage.getFileName())).toString();
            System.out.println(fileName);
            if (!Files.exists(Paths.get(fileName))) {
               executorService.execute(()-> {
                   try {
                       while (true) {
                           boolean append = true;
                           if (fileMessage.getPartNumber() == 1) {
                               append = false;
                           }
                           System.out.println(fileMessage.getPartNumber() + " / " + fileMessage.getPartsCount());
                           FileOutputStream fos = new FileOutputStream(fileMessage.getFileName(), append);
                           fos.write(fileMessage.getFileData());
                           fos.close();
                           if (fileMessage.getPartNumber() == fileMessage.getPartsCount()) {
                               System.out.println("файл получен полностью");
                               countDownLatch.countDown();
                           }
                       }
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               });


                long fileSize = fileMessage.getFileSize();
                try (RandomAccessFile accessFile = new RandomAccessFile(fileName, "rw")) {
                    accessFile.seek(fileMessage.getFilePosition());
                    accessFile.write(fileMessage.getFileData());
                    System.out.println("File " + fileMessage.getFileName() + " created in " + serverStorage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
                   /* this.serviceResponse.setResponse("File " + fileRequest.getFileName() + " created in " + serverStorage);
                    ctx.writeAndFlush(serviceResponse);*/
            else {
                System.out.println("File " + fileMessage.getFileName() + " is already exists in " + serverStorage);
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
