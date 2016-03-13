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

package net.minecrell.statusprotocol.impl;

import net.minecraft.network.ServerStatusResponse;
import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.network.status.StatusResponse;
import org.spongepowered.common.ProtocolMinecraftVersion;

import java.util.OptionalInt;

public final class StatusProtocolImpl_1_8_9 implements StatusProtocolImpl {

    @Override
    public OptionalInt getProtocolVersion(MinecraftVersion version) {
        if (version instanceof ProtocolMinecraftVersion) {
            return OptionalInt.of(((ProtocolMinecraftVersion) version).getProtocol());
        } else {
            return OptionalInt.empty();
        }
    }

    @Override
    public OptionalInt getProtocolVersion(StatusResponse response) {
        if (response instanceof ServerStatusResponse) {
            return OptionalInt.of(((ServerStatusResponse) response).getProtocolVersionInfo().getProtocol());
        } else {
            return OptionalInt.empty();
        }
    }

    @Override
    public boolean setVersion(ClientPingServerEvent.Response response, String name, int protocol) {
        if (response instanceof ServerStatusResponse) {
            ((ServerStatusResponse) response).setProtocolVersionInfo(new ServerStatusResponse.MinecraftProtocolVersionIdentifier(name, protocol));
            return true;
        } else {
            return false;
        }
    }

}
