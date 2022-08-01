package com.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.json.simple.JSONObject;

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

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
      WebSocketServerProtocolHandler.HandshakeComplete complete = (WebSocketServerProtocolHandler.HandshakeComplete) evt;
      JSONObject event = new JSONObject();
      event.put("c", 0);
      event.put("ts", System.currentTimeMillis() / 1000);
      ctx.channel().writeAndFlush(new TextWebSocketFrame(event.toString()));
    } else {
      super.userEventTriggered(ctx, evt);
    }
  }

}
