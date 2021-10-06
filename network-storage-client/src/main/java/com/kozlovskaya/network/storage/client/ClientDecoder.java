package com.kozlovskaya.network.storage.client;

import com.kozlovskaya.network.storage.common.AbstractMessage;
import com.kozlovskaya.network.storage.common.FileResponds;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class ClientDecoder extends SimpleChannelInboundHandler<AbstractMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage message) throws Exception {
        if (message instanceof FileResponds) {
            FileResponds fileResponds = (FileResponds) message;
            System.out.println(fileResponds.getResponds());
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
