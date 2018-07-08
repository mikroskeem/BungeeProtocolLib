/*
 * Copyright (c) 2018 Mark Vainomaa
 *
 * This source code is proprietary software and must not be distributed and/or copied without the express permission of Mark Vainomaa
 */

package eu.mikroskeem.bungeeprotocollib.netty

import eu.mikroskeem.bungeeprotocollib.api.event.PacketEvent
import eu.mikroskeem.bungeeprotocollib.pluginManager
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageCodec
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.protocol.DefinedPacket
import net.md_5.bungee.protocol.PacketWrapper

class PacketReader(private val player: ProxiedPlayer, private val server: Boolean): MessageToMessageCodec<PacketWrapper, DefinedPacket>() {
    override fun decode(ctx: ChannelHandlerContext, wrapper: PacketWrapper, out: MutableList<Any>) {
        val sender = if(server) player.server else player
        val receiver = if(server) player else player.server

        val packetEvent = PacketEvent(wrapper.packet, player, sender, receiver)
        pluginManager.callEvent(packetEvent)

        if(!packetEvent.isCancelled)
            out.add(wrapper)
    }

    override fun encode(ctx: ChannelHandlerContext, packet: DefinedPacket, out: MutableList<Any>) {
        val sender = if(server) player else player.server
        val receiver = if(server) player.server else player

        val packetEvent = PacketEvent(packet, player, sender, receiver)
        pluginManager.callEvent(packetEvent)

        if(!packetEvent.isCancelled)
            out.add(packet)
    }
}