/*
 * Copyright (c) 2018 Mark Vainomaa
 *
 * This source code is proprietary software and must not be distributed and/or copied without the express permission of Mark Vainomaa
 */

package eu.mikroskeem.bungeeprotocollib.injector

import eu.mikroskeem.bungeeprotocollib.logger
import eu.mikroskeem.bungeeprotocollib.netty.PacketReader
import eu.mikroskeem.bungeeprotocollib.reflection.channelWrapperChannelField
import eu.mikroskeem.bungeeprotocollib.reflection.getCasted
import eu.mikroskeem.bungeeprotocollib.reflection.serverConnectionChannelWrapperField
import eu.mikroskeem.bungeeprotocollib.reflection.serverConnectionClass
import eu.mikroskeem.bungeeprotocollib.reflection.userConnectionChannelWrapperField
import eu.mikroskeem.bungeeprotocollib.reflection.userConnectionClass
import io.netty.channel.Channel
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.event.ServerConnectedEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.event.EventPriority.LOWEST

class ChannelInjectorListener: Listener {
    @EventHandler(priority = LOWEST)
    fun on(event: PostLoginEvent) {
        if(!userConnectionClass.isAssignableFrom(event.player::class.java)) {
            logger.warn("${event.player} is not instance of $userConnectionClass! Not injecting Netty channel handler")
            return
        }

        // Grab a channel
        val channelWrapper = userConnectionChannelWrapperField.get(event.player)
        val channel: Channel = channelWrapperChannelField.getCasted(channelWrapper)

        // Inject new listener
        channel.pipeline().addBefore("inbound-boss", "bungeeprotocollib-packet-handler", PacketReader(event.player, false))
    }

    @EventHandler(priority = LOWEST)
    fun on(event: ServerConnectedEvent) {
        if(!serverConnectionClass.isAssignableFrom(event.server::class.java)) {
            logger.warn("${event.server} is not instance of $serverConnectionClass! Not injecting Netty channel handler")
            return
        }

        // Grab a channel
        val channelWrapper = serverConnectionChannelWrapperField.get(event.server)
        val channel: Channel = channelWrapperChannelField.getCasted(channelWrapper)

        // Inject new listener
        channel.pipeline().addBefore("inbound-boss", "bungeeprotocollib-packet-handler", PacketReader(event.player, true))
    }
}