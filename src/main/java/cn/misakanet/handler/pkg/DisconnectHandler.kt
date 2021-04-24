package cn.misakanet.handler.pkg

import cn.misakanet.proto.Cmd
import cn.misakanet.util.ConnGroup
import io.netty.channel.ChannelHandlerContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object DisconnectHandler {
    val logger: Logger = LoggerFactory.getLogger(DisconnectHandler::class.java)


    fun handler(ctx: ChannelHandlerContext, protoData: Cmd.Data) {
        if (!ConnGroup.checkCtx(ctx)) {
            return
        }
        if (protoData.msgType == Cmd.MsgType.Driver) {
            ConnGroup.remove(protoData.disconnect.id)
        }
    }
}