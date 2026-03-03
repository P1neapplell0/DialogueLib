package com.p1nero.dialog_lib.client;


import com.p1nero.dialog_lib.DialogueLib;
import com.p1nero.dialog_lib.DialogueLibConfig;
import com.p1nero.dialog_lib.client.screen.DialogueScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DialogueLib.MOD_ID, value = Dist.CLIENT)
public class ClientForgeEvents {

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Pre event){
        if(Minecraft.getInstance().screen instanceof DialogueScreen && DialogueLibConfig.DISABLE_HUD_IN_DIALOG.get()) {
            event.setCanceled(true);
        }
    }

}
