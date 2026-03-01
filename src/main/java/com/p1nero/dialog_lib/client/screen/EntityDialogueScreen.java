package com.p1nero.dialog_lib.client.screen;

import com.p1nero.dialog_lib.client.screen.component.DialogueAnswerComponent;
import com.p1nero.dialog_lib.mixin.MobInvoker;
import com.p1nero.dialog_lib.network.DialoguePacketHandler;
import com.p1nero.dialog_lib.network.DialoguePacketRelay;
import com.p1nero.dialog_lib.network.packet.serverbound.HandleNpcEntityPlayerInteractPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.NotNull;

public class EntityDialogueScreen extends DialogueScreen{

    protected Entity entity;

    public EntityDialogueScreen(String modId) {
        super(modId);
    }

    public EntityDialogueScreen(String modId, Entity entity) {
        super(modId);
        this.setEntity(entity);
    }

    public void setEntity(@NotNull Entity entity) {
        this.dialogueAnswer = new DialogueAnswerComponent(this.buildDialogueAnswerName(entity.getDisplayName()).append(": "));
        this.entity = entity;
    }

    @Override
    public void sendPacket(int interactionID) {
        DialoguePacketRelay.sendToServer(DialoguePacketHandler.INSTANCE, new HandleNpcEntityPlayerInteractPacket(this.entity == null ? HandleNpcEntityPlayerInteractPacket.NO_ENTITY : this.entity.getId(), interactionID));
    }

    @Override
    protected void playSound() {
        if (this.isSilent || this.entity == null) {
            return;
        }
        if (this.entity instanceof Mob mob && ((MobInvoker) mob).dialog_lib$invokeGetAmbientSound() != null) {
            mob.level().playLocalSound(mob.getX(), mob.getY(), mob.getZ(), ((MobInvoker) mob).dialog_lib$invokeGetAmbientSound(), mob.getSoundSource(), 1.0F, 1.0F, false);
        }
    }
}
