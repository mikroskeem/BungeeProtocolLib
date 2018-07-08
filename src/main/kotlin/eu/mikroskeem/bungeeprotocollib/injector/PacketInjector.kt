/*
 * Copyright (c) 2018 Mark Vainomaa
 *
 * This source code is proprietary software and must not be distributed and/or copied without the express permission of Mark Vainomaa
 */

package eu.mikroskeem.bungeeprotocollib.injector

import com.github.steveice10.mc.protocol.MinecraftConstants
import com.github.steveice10.mc.protocol.MinecraftProtocol
import com.github.steveice10.mc.protocol.data.SubProtocol.STATUS
import com.github.steveice10.packetlib.packet.Packet
import eu.mikroskeem.bungeeprotocollib.reflection.createMap
import eu.mikroskeem.bungeeprotocollib.reflection.getCasted
import eu.mikroskeem.bungeeprotocollib.reflection.incomingPacketsField
import eu.mikroskeem.bungeeprotocollib.reflection.initServerGameMethod
import eu.mikroskeem.bungeeprotocollib.reflection.outgoingPacketsField
import eu.mikroskeem.bungeeprotocollib.reflection.protocolMappingClass
import eu.mikroskeem.bungeeprotocollib.reflection.registerPacketMethod
import eu.mikroskeem.bungeeprotocollib.reflection.v1_12_2
import eu.mikroskeem.bungeeprotocollib.translator.PacketWrapperGenerator
import net.md_5.bungee.protocol.Protocol

// Function to inject packets
private fun registerPacket(packetClass: Class<out Packet>, toClient: Boolean, vararg packetMappings: Pair<Int, Int>) {
    val mappings = packetMappings.map { (protocol, id) -> createMap(protocol, id) }.toTypedArray()
    val wrappedClass = PacketWrapperGenerator.getPacketWrapperClass(packetClass)
    val direction = when {
        toClient -> Protocol.GAME.TO_CLIENT
        else -> Protocol.GAME.TO_SERVER
    }

    // oof wtf
    val mappingsArray = java.lang.reflect.Array.newInstance(protocolMappingClass, packetMappings.size)
    System.arraycopy(mappings, 0, mappingsArray, 0, mappings.size)

    registerPacketMethod.invoke(direction, wrappedClass, mappingsArray)
}

// Function to check if packet with given id is already registered
private fun isPacketRegistered(version: Int, packetId: Int, toClient: Boolean): Boolean {
    val direction = when {
        toClient -> Protocol.GAME.TO_CLIENT
        else -> Protocol.GAME.TO_SERVER
    }

    // What a dumb way, but okay
    return direction.createPacket(packetId, version) != null
}

/**
 * Injects packets into BungeeCord, which aren't registered/implemented.
 */
// TODO: support for older protocol versions?
internal fun injectPackets(/*version: Int, */) {
    val version = MinecraftConstants.PROTOCOL_VERSION

    // Initialize protocol class and register packets
    val protocol = MinecraftProtocol(STATUS)
    initServerGameMethod.invoke(protocol, null)

    // Steal entries
    val incoming: Map<Int, Class<out Packet>> = incomingPacketsField.getCasted(protocol)
    val outgoing: Map<Class<out Packet>, Int> = outgoingPacketsField.getCasted(protocol)

    // Register packets
    outgoing.filter { !isPacketRegistered(version, it.value, true) }.forEach { packetClass, packetId ->
        registerPacket(packetClass, true, v1_12_2(packetId))
    }
    incoming.filter { !isPacketRegistered(version, it.key, false) }.forEach { packetId, packetClass ->
        registerPacket(packetClass, false, v1_12_2(packetId))
    }
}