package com.p1nero.dialog_lib;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.p1nero.dialog_lib.api.block.IBlockDialogueExtension;
import com.p1nero.dialog_lib.api.entity.IEntityDialogueExtension;
import com.p1nero.dialog_lib.api.entity.custom.IEntityNpc;
import com.p1nero.dialog_lib.capability.DialogEntityPatch;
import com.p1nero.dialog_lib.capability.DialogueLibCapabilities;
import com.p1nero.dialog_lib.network.DialoguePacketHandler;
import com.p1nero.dialog_lib.network.DialoguePacketRelay;
import com.p1nero.dialog_lib.network.packet.clientbound.NPCBlockDialoguePacket;
import com.p1nero.dialog_lib.network.packet.clientbound.NPCEntityDialoguePacket;
import com.p1nero.dialog_lib.util.AnnotatedInstanceUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings({"rawtypes", "unchecked"})
@Mod(DialogueLib.MOD_ID)
public class DialogueLib {

    public static final String MOD_ID = "p1nero_dl";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final ResourceLocation EMPTY = ResourceLocation.fromNamespaceAndPath(DialogueLib.MOD_ID, "empty");
    public static List<IEntityDialogueExtension> ENTITY_EXTENSIONS = Lists.newArrayList();
    public static Map<EntityType<?>, List<IEntityDialogueExtension>> ENTITY_EXTENSIONS_MAP = new HashMap<>();
    public static List<IBlockDialogueExtension> BLOCK_EXTENSIONS = Lists.newArrayList();
    public static Map<Block, List<IBlockDialogueExtension>> BLOCK_EXTENSIONS_MAP = new HashMap<>();

    public DialogueLib(FMLJavaModLoadingContext context) {
        context.getModEventBus().addListener(this::dialog_lib$commonSetup);
        MinecraftForge.EVENT_BUS.addListener(this::dialog_lib$onEntityInteract);
        MinecraftForge.EVENT_BUS.addListener(this::dialog_lib$onBlockInteract);
        MinecraftForge.EVENT_BUS.addListener(this::dialog_lib$onLivingTick);
        context.registerConfig(ModConfig.Type.CLIENT, DialogueLibConfig.SPEC);
    }

    private void dialog_lib$commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(DialoguePacketHandler::register);
        event.enqueueWork(() -> {
            ENTITY_EXTENSIONS = AnnotatedInstanceUtil.getModEntityExtensions();
            for (IEntityDialogueExtension extension : ENTITY_EXTENSIONS) {
                EntityType<?> entityType = extension.getEntityType();
                ENTITY_EXTENSIONS_MAP.computeIfAbsent(entityType, k -> new ArrayList<>()).add(extension);
            }
            BLOCK_EXTENSIONS = AnnotatedInstanceUtil.getModBlockExtensions();
            for (IBlockDialogueExtension extension : BLOCK_EXTENSIONS) {
                Block block = extension.getBlock();
                BLOCK_EXTENSIONS_MAP.computeIfAbsent(block, k -> new ArrayList<>()).add(extension);
            }
        });
    }

    private void dialog_lib$onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        runIfEntityExtensionExist(event.getEntity(), event.getTarget(), (iEntityDialogueExtension -> {
            iEntityDialogueExtension.onPlayerInteract(event.getEntity(), event.getTarget(), event.getHand());
            InteractionResult interactionResult = iEntityDialogueExtension.shouldCancelInteract(event.getEntity(), event.getTarget(), event.getHand());
            if(interactionResult != null) {
                event.setCancellationResult(interactionResult);
                event.setCanceled(true);
            }
        }));
    }

    private void dialog_lib$onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        BlockPos pos = event.getPos();
        BlockState blockState = event.getLevel().getBlockState(pos);
        runIfBlockExtensionExist(event.getEntity(), blockState, pos, (iBlockDialogueExtension -> {
            iBlockDialogueExtension.onPlayerInteract(event.getEntity(), blockState, pos, event.getHand());
            if(iBlockDialogueExtension.shouldCancelInteract(event.getEntity(), blockState, pos, event.getHand())) {
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        }));
    }

    /**
     * 自动停止移动并看玩家
     */
    private void dialog_lib$onLivingTick(LivingEvent.LivingTickEvent event) {
        if(ENTITY_EXTENSIONS_MAP.containsKey(event.getEntity().getType())) {
            DialogEntityPatch patch = DialogueLibCapabilities.getDialogPatch(event.getEntity());
            Player player = patch.getCurrentTalkingPlayer();
            if(player instanceof ServerPlayer) {
                runIfEntityExtensionExist(player, event.getEntity(), iEntityDialogueExtension -> {
                    if(event.getEntity().distanceTo(player) > iEntityDialogueExtension.maxTalkDistance()) {
                        patch.setConservingPlayer(null);
                    }
                    if(event.getEntity() instanceof Mob mob) {
                        mob.getLookControl().setLookAt(player);
                        mob.getNavigation().stop();
                    }
                });
            }
        }
        if(event.getEntity() instanceof IEntityNpc npc && npc.getConversingPlayer() != null) {
            if(npc.shouldLookAtConservingPlayer()) {
                if(event.getEntity() instanceof Mob mob) {
                    mob.getLookControl().setLookAt(npc.getConversingPlayer());
                    mob.getNavigation().stop();
                }
            }
        }
    }

    public static void runIfEntityExtensionExist(Player player, Entity self, Consumer<IEntityDialogueExtension> extensionConsumer) {
        if(self == null) {
            return;
        }
        if(ENTITY_EXTENSIONS_MAP.containsKey(self.getType())) {
            ENTITY_EXTENSIONS_MAP.get(self.getType()).forEach(dialogueExtension -> {
                if(dialogueExtension.canInteractWith(player, self)) {
                    extensionConsumer.accept(dialogueExtension);
                }
            });
        }
    }

    public static void runIfBlockExtensionExist(Player player, BlockState self, BlockPos pos, Consumer<IBlockDialogueExtension> extensionConsumer) {
        if(self == null) {
            return;
        }
        if(BLOCK_EXTENSIONS_MAP.containsKey(self.getBlock())) {
            BLOCK_EXTENSIONS_MAP.get(self.getBlock()).forEach(dialogueExtension -> {
                if(dialogueExtension.canInteractWith(player, self, pos)) {
                    extensionConsumer.accept(dialogueExtension);
                }
            });
        }
    }

    public static void sendDialog(BlockPos pos, CompoundTag data, ServerPlayer player) {
        DialoguePacketRelay.sendToPlayer(DialoguePacketHandler.INSTANCE, new NPCBlockDialoguePacket(pos, data), player);
    }

    public static void sendDialog(BlockPos pos, ServerPlayer player) {
        DialoguePacketRelay.sendToPlayer(DialoguePacketHandler.INSTANCE, new NPCBlockDialoguePacket(pos, new CompoundTag()), player);
    }

    public static void sendDialog(Entity self, CompoundTag data, ServerPlayer player) {
        DialoguePacketRelay.sendToPlayer(DialoguePacketHandler.INSTANCE, new NPCEntityDialoguePacket(self.getId(), data), player);
    }

    public static void sendDialog(Entity self, ServerPlayer player) {
        DialoguePacketRelay.sendToPlayer(DialoguePacketHandler.INSTANCE, new NPCEntityDialoguePacket(self.getId(), new CompoundTag()), player);
    }

}
