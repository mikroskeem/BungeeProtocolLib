/*
 * Copyright (c) 2018 Mark Vainomaa
 *
 * This source code is proprietary software and must not be distributed and/or copied without the express permission of Mark Vainomaa
 */

package eu.mikroskeem.bungeeprotocollib.translator

import com.github.steveice10.packetlib.io.stream.StreamNetInput
import com.github.steveice10.packetlib.io.stream.StreamNetOutput
import com.github.steveice10.packetlib.packet.Packet
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

// Baahh :sheep:
internal fun <T: Packet> T.clonePacket(): T {
    val output = ByteArrayOutputStream()
    val newPacket = PacketWrapperGenerator.getPacketConstructor(this::class.java).construct().also {
        this.write(StreamNetOutput(output))
        it.read(StreamNetInput(ByteArrayInputStream(output.toByteArray())))
    }

    @Suppress("UNCHECKED_CAST")
    return newPacket as T
}