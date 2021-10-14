package com.kozlovskaya.network.storage.Server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kozlovskaya.network.storage.common.messages.Request;
import com.kozlovskaya.network.storage.common.messages.Response;
import com.kozlovskaya.network.storage.common.messages.file.FileRequest;
import com.kozlovskaya.network.storage.common.messages.service.ServiceRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class JsonDecoder extends MessageToMessageDecoder<byte[]> {

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void decode(ChannelHandlerContext ctx, byte[] message, List<Object> out) throws Exception {
        System.out.println("byte[] пришел на сервер");
        ServiceRequest serviceRequest = objectMapper.readValue(message, ServiceRequest.class);
        out.add(serviceRequest);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
