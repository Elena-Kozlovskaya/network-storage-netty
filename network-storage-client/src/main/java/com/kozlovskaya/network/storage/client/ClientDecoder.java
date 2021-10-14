package com.kozlovskaya.network.storage.client;

import com.kozlovskaya.network.storage.common.messages.AbstractMessage;
import com.kozlovskaya.network.storage.common.messages.service.ServiceResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class ClientDecoder extends SimpleChannelInboundHandler<AbstractMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage message) throws Exception {
        if (message instanceof ServiceResponse) {
            ServiceResponse serviceResponse = (ServiceResponse) message;
            System.out.println(serviceResponse.getResponse());
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
