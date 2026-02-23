package com.p1nero.dialog_lib.api.component;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;

public class DialogueComponentBuilder {

    protected final String name;
    protected String modId;

    public DialogueComponentBuilder(EntityType<?> entityType, String modId) {
        this.name = entityType.toString();
        this.modId = modId;
    }

    public DialogueComponentBuilder(Entity entity, String modId) {
        this.name = entity.getType().toString();
        this.modId = modId;
    }

    public DialogueComponentBuilder(BlockState blockState, String modId) {
        this.name = blockState.getBlock().getDescriptionId();
        this.modId = modId;
    }

    public DialogueComponentBuilder(String name, String modId) {
        this.name = name;
        this.modId = modId;
    }

    public String modId() {
        return modId;
    }

    public static DialogueComponentBuilder defaultBuilder(String name, String modId) {
        return new DialogueComponentBuilder(name, modId);
    }

    public static MutableComponent buildDialogue(Entity entity, Component content) {
        return Component.literal("[").append(entity.getDisplayName().copy().withStyle(ChatFormatting.YELLOW)).append("]:").append(content);
    }

    public static MutableComponent buildDialogue(EntityType<?> entity, Component content) {
        return Component.literal("[").append(entity.getDescription().copy().withStyle(ChatFormatting.YELLOW)).append("]:").append(content);
    }

    public static MutableComponent buildDialogue(Entity entity, Component content, ChatFormatting... nameChatFormatting) {
        return Component.literal("[").append(entity.getDisplayName().copy().withStyle(nameChatFormatting)).append("]:").append(content).withStyle();
    }

    public MutableComponent opt(EntityType<?> entityType, String key) {
        return Component.translatable(entityType + "." + modId() + ".option." + key);
    }

    public MutableComponent optWithBrackets(EntityType<?> entityType, String key) {
        return Component.literal("[").append(opt(entityType, key)).append("]");
    }

    public MutableComponent opt(EntityType<?> entityType, int i) {
        return Component.translatable(entityType + "." + modId() + ".option" + i);
    }

    public MutableComponent optWithBrackets(EntityType<?> entityType, int i) {
        return Component.literal("[").append(opt(entityType, i)).append("]");
    }

    public MutableComponent opt(String key) {
        return Component.translatable(name + "." + modId() + ".option." + key);
    }

    public MutableComponent opt(String key, Object... params) {
        return Component.translatable(name + "." + modId() + ".option" + key, params);
    }

    public MutableComponent opt(int i) {
        return Component.translatable(name + "." + modId() + ".option" + i);
    }

    public MutableComponent optWithBrackets(int i) {
        return Component.literal("[").append(opt(i)).append("]");
    }

    public MutableComponent opt(int i, Object... params) {
        return Component.translatable(name + "." + modId() + ".option" + i, params);
    }

    public MutableComponent optWithBrackets(int i, Object... params) {
        return Component.literal("[").append(opt(i, params)).append("]");
    }

    public MutableComponent ans(EntityType<?> entityType, int i, boolean newLine) {
        Component component = Component.translatable(entityType + "." + modId() + ".answer" + i);

        return Component.literal(newLine ? "\n" : "").append(component);//换行符有效
    }

    public MutableComponent appendLine(String key, Object... objects) {
        Component component = Component.translatable(key, objects);
        return Component.literal("\n").append(component);
    }

    public MutableComponent ans(EntityType<?> entityType, int i, Object... objects) {
        Component component = Component.translatable(entityType + "." + modId() + ".answer" + i, objects);
        return Component.literal("\n").append(component);//换行符有效
    }

    public MutableComponent ans(EntityType<?> entityType, int i, String s) {
        Component component = Component.translatable(entityType + "." + modId() + ".answer" + i, s);
        return Component.literal("\n").append(component);
    }

    public MutableComponent ans(int i, boolean newLine) {
        Component component = Component.translatable(name + "." + modId() + ".answer" + i);

        return Component.literal(newLine ? "\n" : "").append(component);//换行符有效
    }

    public MutableComponent ans(String s) {
        Component component = Component.translatable(name + "." + modId() + ".answer" + s);
        return Component.literal("\n").append(component);//换行符有效
    }

    public MutableComponent ans(int i) {
        Component component = Component.translatable(name + "." + modId() + ".answer" + i);
        return Component.literal("\n").append(component);//换行符有效
    }

    public MutableComponent ans(int i, Object... param) {
        Component component = Component.translatable(name + "." + modId() + ".answer" + i, param);
        return Component.literal("\n").append(component);//换行符有效
    }

    public MutableComponent buildEntityAnswer(int i) {
        Component component = Component.translatable(name + "." + modId() + ".answer" + i);
        return Component.literal("[").append(Component.translatable(name).withStyle(ChatFormatting.YELLOW)).append(Component.literal("]: ").append(component));
    }

    public MutableComponent ans(int skinID, int i, boolean newLine) {
        Component component = Component.translatable(name + "." + modId() + ".answer" + skinID + "_" + i);
        return Component.literal(newLine ? "\n" : "").append(component);//换行符有效
    }

    public MutableComponent ans(int i, String s) {
        Component component = Component.translatable(name + "." + modId() + ".answer" + i, s);
        return Component.literal("\n").append(component);
    }

}
