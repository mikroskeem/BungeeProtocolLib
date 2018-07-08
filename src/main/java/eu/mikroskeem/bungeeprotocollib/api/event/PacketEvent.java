/*
 * Copyright (c) 2018 Mark Vainomaa
 *
 * This source code is proprietary software and must not be distributed and/or copied without the express permission of Mark Vainomaa
 */

package eu.mikroskeem.bungeeprotocollib.api.event;

import com.github.steveice10.packetlib.packet.Packet;
import eu.mikroskeem.bungeeprotocollib.api.packet.WrappedPacket;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.protocol.DefinedPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Packet event
 */
public final class PacketEvent extends Event implements Cancellable {
    private final DefinedPacket packet;
    private final ProxiedPlayer player;
    private final Connection sender;
    private final Connection reciever;
    private boolean cancelled;

    /**
     * Constructs new {@link PacketEvent}
     *
     * @param packet Packet which is sent from {@code sender} to {@code receiver}
     * @param player {@link ProxiedPlayer} who is involved in this
     * @param sender {@link Connection} who sent the packet.
     * @param receiver {@link Connection} who will receive the packet
     */
    public PacketEvent(@NotNull DefinedPacket packet, @NotNull ProxiedPlayer player, @NotNull Connection sender, @NotNull Connection receiver) {
        this.packet = packet;
        this.player = player;
        this.sender = sender;
        this.reciever = receiver;
    }

    /**
     * Gets the player who is involved in this packet event
     *
     * @return An instance of {@link ProxiedPlayer}
     */
    @NotNull
    public ProxiedPlayer getPlayer() {
        return player;
    }

    /**
     * Gets the {@link DefinedPacket} which is going to be sent
     *
     * @return An instance of {@link DefinedPacket}
     */
    @NotNull
    public DefinedPacket getPacket() {
        return packet;
    }

    /**
     * Returns true if packet is instance of {@link WrappedPacket}, else false
     *
     * @return If packet is instance of {@link WrappedPacket}
     */
    public boolean isWrappedPacket() {
        return packet instanceof WrappedPacket;
    }

    /**
     * Gets an instance of {@link Packet} inside of {@link WrappedPacket}, provided that {@link #isWrappedPacket()}
     * returned true
     *
     * @return An instance of {@link Packet}
     */
    @Nullable
    public Packet getWrappedPacket() {
        return isWrappedPacket() ? ((WrappedPacket) packet).getWrappedPacket() : null;
    }

    /**
     * Gets the {@link DefinedPacket} sender
     *
     * @return An instance of {@link Connection}
     */
    @NotNull
    public Connection getSender() {
        return sender;
    }

    /**
     * Gets the {@link DefinedPacket} receiver
     *
     * @return An instance of {@link Connection}
     */
    @NotNull
    public Connection getReceiver() {
        return reciever;
    }

    /**
     * Returns true if packet sending should be cancelled
     *
     * @return Whether packet sending should be cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets whether packet sending should be cancelled or not
     *
     * @param cancelled Whether packet sending should be cancelled or not
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
