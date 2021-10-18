package com.kozlovskaya.network.storage.client.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kozlovskaya.network.storage.common.messages.AbstractMessage;
import com.kozlovskaya.network.storage.common.messages.Request;
import com.kozlovskaya.network.storage.common.messages.service.ServiceRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class JsonEncoder extends MessageToMessageEncoder<AbstractMessage> {

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void encode(ChannelHandlerContext ctx, AbstractMessage message, List<Object> out) throws Exception {
        System.out.println("Client JsonEncoder");
        byte[] bytes = objectMapper.writeValueAsBytes(message);
        out.add(bytes);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
