package cn.misakanet.handler

import cn.misakanet.handler.pkg.*
import cn.misakanet.proto.Cmd
import cn.misakanet.util.ConnGroup
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ServerHandler : ChannelInboundHandlerAdapter() {
    val logger: Logger = LoggerFactory.getLogger(ServerHandler::class.java)

    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        if (msg == null) {
            logger.info("msg is null")
            return
        }
        ctx!!
        val protoData = msg as Cmd.Data
//        logger.debug(protoData.toString())
        when (protoData.cmd) {
            Cmd.CmdType.T_Hello -> {
                HelloHandler.handler(ctx, protoData)
            }
            Cmd.CmdType.T_Ping -> {
                PingHandler.handler(ctx, protoData)
            }
            Cmd.CmdType.T_Pad_Type -> {
                PadTypeHandler.handler(ctx, protoData)
            }
            Cmd.CmdType.T_DISCONNECT -> {
                DisconnectHandler.handler(ctx, protoData)
            }
            else -> {
                DefaultHandler.handler(ctx, protoData)
            }
        }
    }

    override fun handlerAdded(ctx: ChannelHandlerContext?) {
        logger.info("add a channel: ${ctx?.channel()}")
        super.handlerAdded(ctx)
    }

    override fun handlerRemoved(ctx: ChannelHandlerContext?) {
        ctx!!
        ConnGroup.remove(ctx.channel())
        super.handlerRemoved(ctx)
    }
}