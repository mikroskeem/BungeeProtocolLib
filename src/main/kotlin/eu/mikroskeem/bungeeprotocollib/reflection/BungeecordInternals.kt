/*
 * Copyright (c) 2018 Mark Vainomaa
 *
 * This source code is proprietary software and must not be distributed and/or copied without the express permission of Mark Vainomaa
 */

package eu.mikroskeem.bungeeprotocollib.reflection

import com.github.steveice10.mc.protocol.MinecraftConstants
import net.md_5.bungee.protocol.Protocol

internal val channelWrapperClass = Class.forName("net.md_5.bungee.netty.ChannelWrapper")

internal val userConnectionClass = Class.forName("net.md_5.bungee.UserConnection")

internal val serverConnectionClass = Class.forName("net.md_5.bungee.ServerConnection")

internal val userConnectionChannelWrapperField = userConnectionClass
        .getDeclaredField("ch")
        .accessible()

internal val serverConnectionChannelWrapperField = serverConnectionClass
        .getDeclaredField("ch")
        .accessible()

internal val channelWrapperChannelField = channelWrapperClass
        .getDeclaredField("ch")
        .accessible()

internal val protocolMappingClass = Class.forName("net.md_5.bungee.protocol.Protocol\$ProtocolMapping")

internal val protocolMappingArrayClass = Class.forName("[L" + "net.md_5.bungee.protocol.Protocol\$ProtocolMapping;")

internal val protocolMappingMethod = Protocol::class.java
        .getDeclaredMethod("map", Int::class.java, Int::class.java)
        .accessible()

internal val registerPacketMethod = Protocol.DirectionData::class.java
        .getDeclaredMethod("registerPacket", Class::class.java, protocolMappingArrayClass)
        .accessible()

// Function to create internal map class instance
internal fun createMap(protocol: Int, id: Int): Any = protocolMappingMethod.invoke(null, protocol, id)

// Function to create packet mapping for 1.12.2
internal fun v1_12_2(packetId: Int) = MinecraftConstants.PROTOCOL_VERSION to packetId