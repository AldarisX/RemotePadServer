package cn.misakanet.bean

import cn.misakanet.proto.Cmd
import io.netty.channel.Channel

class ConnObj {
    var group: String? = null
    var id: String? = null
    var name: String? = null
    var type: Cmd.MsgType? = Cmd.MsgType.UNRECOGNIZED
    var channel: Channel? = null
}