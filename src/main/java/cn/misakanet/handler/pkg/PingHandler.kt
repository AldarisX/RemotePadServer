package cn.misakanet.handler.pkg

import cn.misakanet.proto.Cmd
import cn.misakanet.util.ConnGroup
import io.netty.channel.ChannelHandlerContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object PingHandler {
    val logger: Logger = LoggerFactory.getLogger(PingHandler::class.java)

    fun handler(ctx: ChannelHandlerContext, protoData: Cmd.Data) {
        if (!ConnGroup.checkCtx(ctx)) {
            return
        }
        if (protoData.msgType == Cmd.MsgType.Server) {
            return
        }
        when (protoData.msgType) {
            Cmd.MsgType.Driver -> {
                //有手柄端时
                if (ConnGroup.hasPad(protoData.id)) {
                    ConnGroup.send(protoData)
                }
                ctx.writeAndFlush(protoData.toBuilder().setMsgType(Cmd.MsgType.Server).build())
            }
            Cmd.MsgType.Pad -> {
                //有驱动端时
                if (ConnGroup.hasDriver(protoData.id)) {
                    ConnGroup.send(protoData)
                }
            }
        }
    }
}