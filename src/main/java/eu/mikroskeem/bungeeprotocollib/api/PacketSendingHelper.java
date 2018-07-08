/*
 * Copyright (c) 2018 Mark Vainomaa
 *
 * This source code is proprietary software and must not be distributed and/or copied without the express permission of Mark Vainomaa
 */

package eu.mikroskeem.bungeeprotocollib.api;

import com.github.steveice10.packetlib.packet.Packet;
import eu.mikroskeem.bungeeprotocollib.api.packet.WrappedPacket;
import eu.mikroskeem.bungeeprotocollib.translator.PacketWrapperGenerator;
import eu.mikroskeem.bungeeprotocollib.translator.PacketClonerKt;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

public final class PacketSendingHelper {
    public static void sendPacket(@NotNull ProxiedPlayer player, @NotNull Packet packet) {
        WrappedPacket wrapperPacket;
        try {
            wrapperPacket = PacketWrapperGenerator.getPacketWrapperClass(packet.getClass()).newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to construct a wrapper packet", e);
        }
        wrapperPacket.setWrappedPacket(PacketClonerKt.clonePacket(packet));

        player.unsafe().sendPacket(wrapperPacket);
    }
}