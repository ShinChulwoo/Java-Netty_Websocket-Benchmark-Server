package com.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

import java.util.Map;

public class WebSocketHandler extends ChannelInboundHandlerAdapter {

  private static final ObjectMapper om = new ObjectMapper();

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws JsonProcessingException {
    if(msg instanceof TextWebSocketFrame) {
      String request = ((TextWebSocketFrame) msg).text();
      Map<String, Object> reqMsg = om.readValue(request, Map.class);
      String message = om.writeValueAsString(new Message((Integer) reqMsg.get("c")));
      ctx.channel().writeAndFlush(new TextWebSocketFrame(message));
    } else {
      System.out.println("unsupported frame type:" + msg.getClass().getName());
    }
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
      WebSocketServerProtocolHandler.HandshakeComplete complete = (WebSocketServerProtocolHandler.HandshakeComplete) evt;
      String message = om.writeValueAsString(new Message());
      ctx.channel().writeAndFlush(new TextWebSocketFrame(message));
    } else {
      super.userEventTriggered(ctx, evt);
    }
  }

  public static class Message {
    private int c;
    private long ts;

    public Message() {
      this.c = 0;
      this.ts = System.currentTimeMillis() / 1000;
    }

    public Message(int c) {
      this.c = c;
      this.ts = System.currentTimeMillis() / 1000;
    }

    public int getC() {
      return c;
    }

    public long getTs() {
      return ts;
    }

    public void setC(int c) {
      this.c = c;
    }

    public void setTs(long ts) {
      this.ts = ts;
    }
  }

}
