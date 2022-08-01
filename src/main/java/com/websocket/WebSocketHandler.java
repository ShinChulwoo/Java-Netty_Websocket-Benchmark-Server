package com.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.Locale;

public class WebSocketHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    if(msg instanceof TextWebSocketFrame) {
      String request = ((TextWebSocketFrame) msg).text();
      ctx.channel().writeAndFlush(new TextWebSocketFrame(request));
    } else {
      System.out.println("unsupported frame type:" + msg.getClass().getName());
    }
  }
}
