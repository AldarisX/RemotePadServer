package cn.misakanet.util

import cn.misakanet.bean.ConnObj
import cn.misakanet.proto.Cmd
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext

object ConnGroup {
    val connList = HashMap<String, ConnObj>()
    val padList = HashMap<String, ConnObj>()
    val driverList = HashMap<String, ConnObj>()

    fun addConn(protoData: Cmd.Data, ctx: ChannelHandlerContext) {
        val connObj = ConnObj()
        connObj.id = protoData.id
        connObj.channel = ctx.channel()
        connObj.type = protoData.msgType
        connObj.group = protoData.hello.group
        connObj.name = protoData.hello.name

        connList[protoData.id] = connObj
        when (protoData.msgType) {
            Cmd.MsgType.Pad -> {
                padList[protoData.id] = connObj
            }
            Cmd.MsgType.Driver -> {
                driverList[protoData.id] = connObj
            }
        }
    }

    fun remove(id: String, list: HashMap<String, ConnObj>) {
        list.forEach { (cid, connObj) ->
            if (id == cid) {
                connObj.channel!!.close()
            }
        }
    }

    fun remove(id: String) {
        remove(id, connList)
        remove(id, padList)
        remove(id, driverList)

        connList.remove(id)
        padList.remove(id)
        driverList.remove(id)
    }

    fun remove(ch: Channel) {
        connList.forEach { (_, connObj) ->
            if (ch == connObj.channel) {
                remove(connObj.id!!)
                return
            }
        }
    }

    fun hasPad(id: String): Boolean {
        return hasPair(id, padList)
    }

    fun hasDriver(id: String): Boolean {
        return hasPair(id, driverList)
    }

    fun hasPair(id: String, list: HashMap<String, ConnObj>): Boolean {
        val connObj = connList[id] ?: return false
        val group = connObj.group
        list.forEach { (_, connObj) ->
            if (group == connObj.group)
                return true
        }
        return false
    }

    fun send(protoData: Cmd.Data) {
        val group = connList[protoData.id]!!.group!!
        when (protoData.msgType) {
            Cmd.MsgType.Pad -> {
                send(group, protoData, driverList)
            }
            Cmd.MsgType.Driver -> {
                send(group, protoData, padList)
            }
        }
    }

    fun send(group: String, protoData: Cmd.Data, list: HashMap<String, ConnObj>) {
        val to = protoData.to
        list.forEach { (cid, connObj) ->
            if (group == connObj.group) {
                if (to != null && to == cid) {
                    connObj.channel!!.writeAndFlush(protoData)
                    return
                }
                connObj.channel!!.writeAndFlush(protoData)
            }
        }
    }

    fun checkCtx(ctx: ChannelHandlerContext): Boolean {
        connList.forEach { (_, connObj) ->
            if (connObj.channel == ctx.channel()) {
                return true
            }
        }
        ctx.close()
        return false
    }
}