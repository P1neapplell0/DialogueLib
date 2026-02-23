package com.p1nero.dialog_lib.network.packet.serverbound;

import com.p1nero.dialog_lib.events.ServerCustomInteractEvent;
import com.p1nero.dialog_lib.network.packet.BasePacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;

public record HandleCustomInteractPacket(String modId, int interactionID) implements BasePacket {

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(this.modId());
        buf.writeInt(this.interactionID());
    }

    public static HandleCustomInteractPacket decode(FriendlyByteBuf buf) {
        return new HandleCustomInteractPacket(buf.readUtf(), buf.readInt());
    }

    @Override
    public void execute(@Nullable Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            ServerCustomInteractEvent event = new ServerCustomInteractEvent(serverPlayer, interactionID, modId);
            MinecraftForge.EVENT_BUS.post(event);
        }
    }
}
