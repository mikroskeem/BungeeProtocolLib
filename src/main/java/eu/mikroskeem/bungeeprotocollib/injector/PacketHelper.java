/*
 * Copyright (c) 2018 Mark Vainomaa
 *
 * This source code is proprietary software and must not be distributed and/or copied without the express permission of Mark Vainomaa
 */

package eu.mikroskeem.bungeeprotocollib.injector;

import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.io.ByteBufNetInput;
import com.github.steveice10.packetlib.tcp.io.ByteBufNetOutput;
import eu.mikroskeem.bungeeprotocollib.api.packet.WrappedPacket;
import eu.mikroskeem.bungeeprotocollib.reflection.ClassloaderKt;
import eu.mikroskeem.bungeeprotocollib.translator.PacketWrapperGenerator;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

/**
 * Packet helper class. {@link WrappedPacket} delegates all its calls to this class
 */
public final class PacketHelper extends WrappedPacket {
    @NotNull private final WrappedPacket wrappedPacket;
    @NotNull private final String packetClassName;
    @Nullable private Packet packet;

    private PacketHelper(@NotNull WrappedPacket wrappedPacket, @NotNull String packetClassName) {
        this.wrappedPacket = wrappedPacket;
        this.packetClassName = packetClassName;
    }

    @Override
    @NotNull
    public Packet getWrappedPacket() {
        // Generate packet if it is null. Lazy init saves one useless packet construction.
        if(packet == null) {
            Class<Packet> packetCl = ClassloaderKt.findClass(packetClassName);
            packet = PacketWrapperGenerator.getPacketConstructor(packetCl).construct();
        }

        return packet;
    }

    @Override
    public void setWrappedPacket(@NotNull Packet packet) {
        Objects.requireNonNull(packet, "Packet cannot be null!");
        if(!packet.getClass().getName().equals(packetClassName)) {
            throw new IllegalStateException("Cannot change packet type!");
        }
        this.packet = packet;
    }

    @Override
    public void read(ByteBuf buf) {
        try {
            getWrappedPacket().read(new ByteBufNetInput(buf));
        } catch (IOException e) {
            // :(
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(ByteBuf buf) {
        try {
            getWrappedPacket().write(new ByteBufNetOutput(buf));
        } catch (IOException e) {
            // :(
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handle(AbstractPacketHandler abstractPacketHandler) throws Exception {
        // TODO: what should this actually do?
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof WrappedPacket)
            return wrappedPacket.getWrappedPacket().equals(((WrappedPacket) other).getWrappedPacket());
        return false;
    }

    @Override
    public int hashCode() {
        return getWrappedPacket().hashCode();
    }

    @Override
    public String toString() {
        return "WrappedPacket(" + getWrappedPacket().toString() + ")";
    }

    /**
     * Generates a new {@link PacketHelper} instance.
     *
     * @param wrappedPacket Wrapped packet instance
     * @param packetName Wrappable class name
     * @return An instance of {@link PacketHelper} class for use inside of {@link WrappedPacket}
     */
    @NotNull
    @SuppressWarnings("unused")
    public static PacketHelper newHelperClass(@NotNull WrappedPacket wrappedPacket, @NotNull String packetName) {
        return new PacketHelper(wrappedPacket, packetName);
    }
}
