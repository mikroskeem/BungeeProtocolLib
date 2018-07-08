/*
 * Copyright (c) 2018 Mark Vainomaa
 *
 * This source code is proprietary software and must not be distributed and/or copied without the express permission of Mark Vainomaa
 */

package eu.mikroskeem.bungeeprotocollib.api.packet;

import com.github.steveice10.packetlib.packet.Packet;
import net.md_5.bungee.protocol.DefinedPacket;
import org.jetbrains.annotations.NotNull;

/**
 * Special type for wrapped packets
 */
public abstract class WrappedPacket extends DefinedPacket {
    /**
     * Gets wrapped packet
     *
     * @return An instance of {@link Packet}
     */
    @NotNull
    public abstract Packet getWrappedPacket();

    /**
     * Sets wrapped packet
     *
     * @param packet An instance of {@link Packet}
     */
    public abstract void setWrappedPacket(@NotNull Packet packet);
}
