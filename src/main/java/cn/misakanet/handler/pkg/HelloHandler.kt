package cn.misakanet.handler.pkg

import cn.misakanet.proto.Cmd
import cn.misakanet.util.ConnGroup
import com.github.mervick.aes_everywhere.Aes256
import io.netty.channel.ChannelHandlerContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object HelloHandler {
    val logger: Logger = LoggerFactory.getLogger(HelloHandler::class.java)

    fun handler(ctx: ChannelHandlerContext, protoData: Cmd.Data) {
        ConnGroup.addConn(protoData, ctx)
        logger.info(protoData.toString())
        //验证hello
        if (Aes256.decrypt(protoData.hello.serverMsg, "654321") != "hello") {
            println("unknow client")
            ConnGroup.remove(ctx.channel())
            return
        }
        //server hello
//        val serverHello =
//            Cmd.Data.newBuilder().setCmd(Cmd.CmdType.T_Hello).setMsgType(Cmd.MsgType.Server).build()
//        ctx.writeAndFlush(serverHello)

        //hello的转发
        when (protoData.msgType) {
            Cmd.MsgType.Pad -> {
                if (ConnGroup.hasDriver(protoData.id))
                    ConnGroup.send(protoData)
            }
            Cmd.MsgType.Driver -> {
                if (ConnGroup.hasPad(protoData.id)) {
                    ConnGroup.send(protoData)
                }
            }
        }
    }
}