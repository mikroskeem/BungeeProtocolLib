/*
 * Copyright (c) 2018 Mark Vainomaa
 *
 * This source code is proprietary software and must not be distributed and/or copied without the express permission of Mark Vainomaa
 */

package eu.mikroskeem.bungeeprotocollib.reflection

import com.github.steveice10.mc.protocol.MinecraftProtocol
import com.github.steveice10.packetlib.Session
import com.github.steveice10.packetlib.packet.PacketProtocol

internal val initServerGameMethod = MinecraftProtocol::class.java
        .getDeclaredMethod("initServerGame", Session::class.java)
        .accessible()

internal val incomingPacketsField = PacketProtocol::class.java
        .getDeclaredField("incoming")
        .accessible()

internal val outgoingPacketsField = PacketProtocol::class.java
        .getDeclaredField("outgoing")
        .accessible()