package com.kozlovskaya.network.storage.client;

import com.kozlovskaya.network.storage.common.messages.AbstractMessage;
import com.kozlovskaya.network.storage.common.messages.service.Responds;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class ClientDecoder extends SimpleChannelInboundHandler<AbstractMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage message) throws Exception {
        if (message instanceof Responds) {
            Responds responds = (Responds) message;
            System.out.println(responds.getResponds());
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
