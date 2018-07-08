/*
 * Copyright (c) 2018 Mark Vainomaa
 *
 * This source code is proprietary software and must not be distributed and/or copied without the express permission of Mark Vainomaa
 */

package eu.mikroskeem.bungeeprotocollib

import eu.mikroskeem.bungeeprotocollib.injector.ChannelInjectorListener
import eu.mikroskeem.bungeeprotocollib.injector.injectPackets
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.api.plugin.PluginManager
import org.slf4j.Logger

class BungeeProtocolLib: Plugin() {
    override fun onLoad() {
        registerListener<ChannelInjectorListener>()

        // Inject custom packets into BungeeCord protocol enum
        injectPackets()
    }
}

val proxy: ProxyServer get() = ProxyServer.getInstance()
val pluginManager: PluginManager get() = proxy.pluginManager
val plugin: BungeeProtocolLib get() = proxy.pluginManager.getPlugin("BungeeProtocolLib") as BungeeProtocolLib
val logger: Logger get() = plugin.slF4JLogger

inline fun <reified T: Listener> BungeeProtocolLib.registerListener()
        = pluginManager.registerListener(this, T::class.java.newInstance())