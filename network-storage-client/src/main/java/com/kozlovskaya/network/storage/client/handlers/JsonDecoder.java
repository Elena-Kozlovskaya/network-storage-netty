package com.kozlovskaya.network.storage.client.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kozlovskaya.network.storage.common.messages.Response;
import com.kozlovskaya.network.storage.common.messages.file.FileResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class JsonDecoder extends MessageToMessageDecoder<byte[]> {

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void decode(ChannelHandlerContext ctx, byte[] message, List<Object> out) throws Exception {

        FileResponse fileResponse = objectMapper.readValue(message, FileResponse.class);
        out.add(fileResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
