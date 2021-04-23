package cn.misakanet.pipline

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.TooLongFrameException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.ByteOrder

class PkgDecoder : LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, Integer.MAX_VALUE, 0, 4, 0, 0, true) {
    val logger: Logger = LoggerFactory.getLogger(PkgDecoder::class.java)

    override fun decode(ctx: ChannelHandlerContext?, `in`: ByteBuf?): Any? {
        if (`in` == null) {
            println("`in` is null")
            return null
        }
        ctx!!
        logger.debug("msg from ${ctx.channel()}")
        return try {
            return super.decode(ctx, `in`)
        } catch (e: TooLongFrameException) {
            logger.error(e.toString())
            logger.info("wrong len: $ctx")
            ctx.writeAndFlush("who are you???")
            ctx.channel().close()
            null
        }
    }

}