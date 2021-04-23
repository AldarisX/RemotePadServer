package cn.misakanet

import cn.misakanet.handler.HeatBeartHandler
import cn.misakanet.handler.ServerHandler
import cn.misakanet.pipline.PkgDecoder
import cn.misakanet.pipline.PkgEncoder
import cn.misakanet.pipline.ProtobufDecoder
import cn.misakanet.proto.Cmd
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.protobuf.ProtobufEncoder
import io.netty.handler.timeout.IdleStateHandler
import io.netty.util.ResourceLeakDetector
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun main() {
    System.setProperty("logback.configurationFile", "logback.xml")

    val logger: Logger = LoggerFactory.getLogger("Main")

    val eventLoopGroup = NioEventLoopGroup()
    try {
        val bootstrap = ServerBootstrap()
        bootstrap.group(eventLoopGroup)
            .channel(NioServerSocketChannel::class.java)
            .childHandler(object : ChannelInitializer<SocketChannel>() {
                override fun initChannel(ch: SocketChannel?) {
                    ch!!
                    ch.pipeline().addLast(IdleStateHandler(32, 10, 0))
                    ch.pipeline().addLast(PkgDecoder())
//                    ch.pipeline().addLast(ProtobufVarint32FrameDecoder())
//                    ch.pipeline().addLast(ProtobufVarint32LengthFieldPrepender())
                    ch.pipeline().addLast(PkgEncoder())
                    ch.pipeline().addLast(ProtobufEncoder())
                    ch.pipeline().addLast(ProtobufDecoder(Cmd.Data.getDefaultInstance()))
                    ch.pipeline().addLast(HeatBeartHandler())
                    ch.pipeline().addLast(ServerHandler())
                }

            })
//            .option(ChannelOption.SO_BACKLOG, 128)
            .childOption(ChannelOption.TCP_NODELAY, true)
//            .childOption(ChannelOption.SO_KEEPALIVE, true)
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED)
        val future = bootstrap.bind(3360).sync()
        logger.info("server start at 3360")

        future.channel().closeFuture().sync()
    } finally {
        eventLoopGroup.shutdownGracefully();
    }
}