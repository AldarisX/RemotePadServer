package cn.misakanet.pipline

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelOutboundHandlerAdapter
import io.netty.channel.ChannelPromise
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PkgEncoder : ChannelOutboundHandlerAdapter() {
    val logger: Logger = LoggerFactory.getLogger(PkgEncoder::class.java)

    override fun write(ctx: ChannelHandlerContext?, msg: Any?, promise: ChannelPromise?) {
        ctx!!
        if (msg is ByteBuf) {
            val bodyLen = msg.readableBytes()
            val headLen = intToByteArray4(bodyLen)
            val body = msg.array()
            val newMsg = ctx.alloc().ioBuffer()
            newMsg.capacity(4 + bodyLen)
            newMsg.ensureWritable(4 + bodyLen)
            newMsg.writeBytes(headLen)
            newMsg.writeBytes(body)
            ctx.writeAndFlush(newMsg)
//            logger.debug(headLen.contentToString())
//            logger.debug(body.contentToString())
            return
        }
        super.write(ctx, msg, promise)
    }

    private fun intToByteArray4(num: Int): ByteArray {
        val byteArray = ByteArray(4)
        val highH = ((num shr 24) and 0xff).toByte()
        val highL = ((num shr 16) and 0xff).toByte()
        val lowH = ((num shr 8) and 0xff).toByte()
        val lowL = (num and 0xff).toByte()
        byteArray[3] = highH
        byteArray[2] = highL
        byteArray[1] = lowH
        byteArray[0] = lowL
        return byteArray
    }
}