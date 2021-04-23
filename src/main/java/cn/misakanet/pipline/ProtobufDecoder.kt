package cn.misakanet.pipline

import com.google.protobuf.InvalidProtocolBufferException
import com.google.protobuf.MessageLite
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.protobuf.ProtobufDecoder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ProtobufDecoder(prototype: MessageLite?) : ProtobufDecoder(prototype) {
    val logger: Logger = LoggerFactory.getLogger(ProtobufDecoder::class.java)

    override fun decode(ctx: ChannelHandlerContext?, msg: ByteBuf?, out: MutableList<Any>?) {
        msg!!.readBytes(4).release()
        try {
            super.decode(ctx, msg, out)
            logger.debug(out.toString())
        } catch (e: InvalidProtocolBufferException) {
            msg.resetReaderIndex();
            println("LEN:${msg.readIntLE()}")
            e.printStackTrace()
            logger.error(e.toString())
        }
    }
}