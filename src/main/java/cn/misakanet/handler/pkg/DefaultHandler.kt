package cn.misakanet.handler.pkg

import cn.misakanet.proto.Cmd
import cn.misakanet.util.ConnGroup
import io.netty.channel.ChannelHandlerContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object DefaultHandler {
    val logger: Logger = LoggerFactory.getLogger(DefaultHandler::class.java)


    fun handler(ctx: ChannelHandlerContext, protoData: Cmd.Data) {
        if (!ConnGroup.checkCtx(ctx)) {
            return
        }
        ConnGroup.send(protoData)
    }
}