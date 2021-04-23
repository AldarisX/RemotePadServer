package cn.misakanet.handler.pkg

import cn.misakanet.proto.Cmd
import cn.misakanet.util.ConnGroup
import io.netty.channel.ChannelHandlerContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object PadTypeHandler {
    val logger: Logger = LoggerFactory.getLogger(PadTypeHandler::class.java)

    fun handler(ctx: ChannelHandlerContext, protoData: Cmd.Data) {
        if (!ConnGroup.checkCtx(ctx)) {
            return
        }
        val group = ConnGroup.connList[protoData.id]!!.group!!
        ConnGroup.send(group, protoData.toBuilder().setMsgType(Cmd.MsgType.Server).build(), ConnGroup.driverList)
    }
}