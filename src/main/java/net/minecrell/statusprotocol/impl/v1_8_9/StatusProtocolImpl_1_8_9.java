package net.minecrell.statusprotocol.impl.v1_8_9;

import net.minecraft.network.ServerStatusResponse;
import net.minecrell.statusprotocol.impl.StatusProtocolImpl;
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
