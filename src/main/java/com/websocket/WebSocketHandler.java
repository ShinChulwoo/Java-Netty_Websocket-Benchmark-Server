package com.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WebSocketHandler extends ChannelInboundHandlerAdapter {


  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws ParseException {
    if(msg instanceof TextWebSocketFrame) {
      String request = ((TextWebSocketFrame) msg).text();
      JSONObject data = (JSONObject) new JSONParser().parse(request);
      String newMsg = getMessage(((Long)data.get("c")).intValue());
      ctx.channel().writeAndFlush(new TextWebSocketFrame(newMsg));
    } else {
      System.out.println("unsupported frame type:" + msg.getClass().getName());
    }
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
      WebSocketServerProtocolHandler.HandshakeComplete complete = (WebSocketServerProtocolHandler.HandshakeComplete) evt;
      ctx.channel().writeAndFlush(new TextWebSocketFrame(this.getMessage(0)));
    } else {
      super.userEventTriggered(ctx, evt);
    }
  }

  private String getMessage(int count) {
    JSONObject event = new JSONObject();
    event.put("c", count);
    event.put("ts", System.currentTimeMillis() / 1000);
    return event.toString();
  }

}
