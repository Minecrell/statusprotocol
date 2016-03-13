/*
 * StatusProtocol
 * Copyright (c) 2016, Minecrell <https://github.com/Minecrell>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.minecrell.statusprotocol;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.inject.Inject;
import net.minecrell.statusprotocol.impl.MinecraftStatusProtocolImpl;
import net.minecrell.statusprotocol.impl.StatusProtocolImpl;
import org.slf4j.Logger;
import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.network.status.StatusResponse;
import org.spongepowered.api.plugin.Plugin;

import java.util.OptionalInt;

/**
 * Main class for status protocol. Contains additional accessors for
 * implementation-specific protocol internals which are not available
 * through SpongeAPI. Use with caution.
 *
 * <p>You should always use this in a way it is completely optional,
 * as it may break easily with every Minecraft version.</p>
 */
@Plugin(id = "net.minecrell.statusprotocol", name = "StatusProtocol")
public final class StatusProtocol {

    private static StatusProtocol instance;
    private final Logger logger;
    private final StatusProtocolImpl impl;

    private static boolean failedMinecraftProtocolVersion;
    private static boolean failedStatusProtocolVersion;
    private static boolean failedStatusSetVersion;

    @Inject
    public StatusProtocol(Logger logger, MinecraftVersion version) {
        checkState(instance == null, "Instance was already initialized");
        instance = this;

        this.logger = logger;

        StatusProtocolImpl impl = null;
        MinecraftStatusProtocolImpl mcImpl = MinecraftStatusProtocolImpl.find(version, logger);
        if (mcImpl != null) {
            try {
                impl = mcImpl.construct();
            } catch (Throwable e) {
                logger.error("Failed to initialize statusprotocol implementation for Minecraft {}. The plugin will be disabled",
                        mcImpl.getTarget(), e);
            }
        }

        this.impl = impl;
        if (impl == null) {
            failedMinecraftProtocolVersion = true;
            failedStatusProtocolVersion = true;
            failedStatusSetVersion = true;
        }
    }

    /**
     * Gets the internal protocol version for the specified
     * {@link MinecraftVersion}.
     *
     * @param version The minecraft version
     * @return The associated protocol version (if successful)
     */
    public static OptionalInt getProtocolVersion(MinecraftVersion version) {
        checkState(instance != null, "StatusProtocol was not initialized");
        checkNotNull(version, "version");

        if (!failedMinecraftProtocolVersion) {
            try {
                return instance.impl.getProtocolVersion(version);
            } catch (Throwable e) {
                failedMinecraftProtocolVersion = true;
                instance.logger.error("Failed to obtain protocol version", e);
            }
        }

        return OptionalInt.empty();
    }

    /**
     * Gets the internal protocol version for the specified
     * {@link StatusResponse}.
     *
     * @param response The status response
     * @return The associated protocol version (if successful)
     */
    public static OptionalInt getProtocolVersion(StatusResponse response) {
        checkState(instance != null, "StatusProtocol was not initialized");
        checkNotNull(response, "response");

        if (!failedStatusProtocolVersion) {
            try {
                return instance.impl.getProtocolVersion(response);
            } catch (Throwable e) {
                failedStatusProtocolVersion = true;
                instance.logger.error("Failed to obtain protocol version", e);
            }
        }

        return OptionalInt.empty();
    }

    /**
     * Sets the version name and protocol version for the specified
     * {@link ClientPingServerEvent.Response} that will be sent to the client.
     *
     * @param response The status response
     * @param name The version name to set
     * @param protocol The protocol version to set
     * @return Whether the operation was successful
     */
    public static boolean setVersion(ClientPingServerEvent.Response response, String name, int protocol) {
        checkState(instance != null, "StatusProtocol was not initialized");
        checkNotNull(response, "response");
        checkNotNull(name, "name");

        if (!failedStatusSetVersion) {
            try {
                return instance.impl.setVersion(response, name, protocol);
            } catch (Throwable e) {
                failedStatusSetVersion = true;
                instance.logger.error("Failed to set response version", e);
            }
        }

        return false;
    }

}
