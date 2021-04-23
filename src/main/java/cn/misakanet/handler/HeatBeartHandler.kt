package cn.misakanet.handler

import cn.misakanet.proto.Cmd
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.timeout.IdleState
import io.netty.handler.timeout.IdleStateEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class HeatBeartHandler : ChannelInboundHandlerAdapter() {
    val logger: Logger = LoggerFactory.getLogger(HeatBeartHandler::class.java)

    override fun userEventTriggered(ctx: ChannelHandlerContext?, evt: Any?) {
        if (evt is IdleStateEvent) {
            ctx!!
            when (evt.state()) {
                IdleState.READER_IDLE -> {
                    logger.info("检测到Channel超时:${ctx.channel().remoteAddress()}")
                    ctx.channel().close()
                    return
                }
                IdleState.WRITER_IDLE -> {
                    logger.debug("发送心跳")
                    val protoData =
                        Cmd.Data.newBuilder().setCmd(Cmd.CmdType.T_Ping).setMsgType(Cmd.MsgType.Server).build()
                    ctx.writeAndFlush(protoData)
                    return
                }
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}