package com.p1nero.dialog_lib.events;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * 自定义的对话，即无实体无方块的情况下凭空构造的对话窗口的返回值的操作，需监听此事件完成。
 */
@Cancelable
public class ServerCustomInteractEvent extends Event {
    private final ResourceLocation id;
    private final ServerPlayer serverPlayer;
    private final int interactId;

    public ServerCustomInteractEvent(ServerPlayer serverPlayer, int interactId, ResourceLocation id) {
        this.serverPlayer = serverPlayer;
        this.interactId = interactId;
        this.id = id;
    }

    public String getModId() {
        return id.getNamespace();
    }

    public ResourceLocation getId() {
        return id;
    }

    public ServerPlayer getServerPlayer() {
        return serverPlayer;
    }

    public int getInteractId() {
        return interactId;
    }

}
