package com.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

public class HttpServerHandler extends ChannelInboundHandlerAdapter {

  private WebSocketServerHandshaker handshaker;

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    if(msg instanceof HttpRequest) {
      HttpRequest httpRequest = (HttpRequest) msg;
      HttpHeaders headers = httpRequest.headers();

      if(headers.get(HttpHeaderNames.CONNECTION).equalsIgnoreCase("Upgrade") &&
          headers.get(HttpHeaderNames.UPGRADE).equalsIgnoreCase("WebSocket")) {
        ctx.pipeline().replace(this, "websocketHandler", new WebSocketHandler());
        handleHandshake(ctx, httpRequest);
      }
    }
  }

  protected void handleHandshake(ChannelHandlerContext ctx, HttpRequest req) {
    WebSocketServerHandshakerFactory wsFactory =
        new WebSocketServerHandshakerFactory(getWebSocketURL(req), null, true);
    handshaker = wsFactory.newHandshaker(req);

    if(handshaker == null) {
      WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
    }
    else {
      handshaker.handshake(ctx.channel(), req);
    }
  }

  protected String getWebSocketURL(HttpRequest req) {
    return "wss://" + req.headers().get("Host") + req.getUri();
  }
}
