package com.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;

public final class WebSocketServer {

  static final int PORT = 8080;

  public static void main(String[] args) throws Exception {
    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .childHandler(new WebSocketServerInitializer());

      Channel ch = b.bind(PORT).sync().channel();
      ch.closeFuture().sync();
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }

  public static class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
      ChannelPipeline pipeline = ch.pipeline();
//      pipeline.addLast("httpServerCodec", new HttpServerCodec());
//      pipeline.addLast("httphandler", new HttpServerHandler());

      pipeline.addLast(new HttpServerCodec());
//      pipeline.addLast(new HttpObjectAggregator(65536));
      pipeline.addLast(new WebSocketServerCompressionHandler());
      pipeline.addLast(new WebSocketServerProtocolHandler("/", null, true));
      pipeline.addLast(new WebSocketHandler());
    }
  }
}
