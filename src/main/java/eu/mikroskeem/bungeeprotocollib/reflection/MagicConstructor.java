/*
 * Copyright (c) 2018 Mark Vainomaa
 *
 * This source code is proprietary software and must not be distributed and/or copied without the express permission of Mark Vainomaa
 */

package eu.mikroskeem.bungeeprotocollib.reflection;

import com.github.steveice10.packetlib.packet.Packet;
import org.jetbrains.annotations.NotNull;

/**
 * Constructor invoker based on sun.reflect.MagicAccessorImpl
 *
 * @param <P> Packet type
 */
public interface MagicConstructor<P extends Packet> {
    /**
     * Constructs a packet class using its private no-args constructor
     *
     * @return Packet instance
     */
    @NotNull
    P construct();
}
