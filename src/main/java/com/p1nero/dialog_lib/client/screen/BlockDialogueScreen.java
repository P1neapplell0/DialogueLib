package com.p1nero.dialog_lib.client.screen;

import com.p1nero.dialog_lib.client.screen.component.DialogueAnswerComponent;
import com.p1nero.dialog_lib.network.DialoguePacketHandler;
import com.p1nero.dialog_lib.network.DialoguePacketRelay;
import com.p1nero.dialog_lib.network.packet.serverbound.HandleNpcBlockPlayerInteractPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockDialogueScreen extends DialogueScreen{

    @Nullable
    protected BlockPos pos;

    public BlockDialogueScreen(String modId) {
        super(modId);
    }

    public BlockDialogueScreen(String modId, BlockState state, BlockPos pos) {
        super(modId);
        this.setBlockState(state, pos);
    }

    public void setBlockState(BlockState blockState, BlockPos pos) {
        this.dialogueAnswer = new DialogueAnswerComponent(this.buildDialogueAnswerName(blockState.getBlock().getName()).append(": "));
        this.pos = pos;
    }

    @Override
    public void sendPacket(int interactionID) {
        DialoguePacketRelay.sendToServer(DialoguePacketHandler.INSTANCE, new HandleNpcBlockPlayerInteractPacket(pos, interactionID));
    }

}
