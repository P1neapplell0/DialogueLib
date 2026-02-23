package com.p1nero.dialog_lib;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DialogueLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DialogueLibConfig {
    public static final ForgeConfigSpec.BooleanValue OPTION_IN_CENTER;
    public static final ForgeConfigSpec.BooleanValue ENABLE_BACKGROUND;
    public static final ForgeConfigSpec.BooleanValue FADED_BACKGROUND;
    public static final ForgeConfigSpec.BooleanValue ENABLE_ANS_BACKGROUND;
    public static final ForgeConfigSpec.BooleanValue ENABLE_OPT_BACKGROUND;
    public static final ForgeConfigSpec.BooleanValue ENABLE_TYPEWRITER_EFFECT;
    public static final ForgeConfigSpec.IntValue TYPEWRITER_EFFECT_SPEED;
    public static final ForgeConfigSpec.IntValue TYPEWRITER_EFFECT_INTERVAL;
    public static final ForgeConfigSpec.IntValue DIALOG_WIDTH;
    static final ForgeConfigSpec SPEC;

    static{
        ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();
        OPTION_IN_CENTER = createBool(clientBuilder, "option_in_center", false, "[选项]是否居中");
        ENABLE_BACKGROUND = createBool(clientBuilder, "enable_background", true, "是否开启[背景框]");
        FADED_BACKGROUND = createBool(clientBuilder, "faded_background", true, "使用[渐变背景框]或[边框背景框]");
        ENABLE_ANS_BACKGROUND = createBool(clientBuilder, "enable_ans_background", false, "是否绘制[回答]的背景");
        ENABLE_OPT_BACKGROUND = createBool(clientBuilder, "enable_opt_background", false, "是否绘制[选项]的背景");
        ENABLE_TYPEWRITER_EFFECT = createBool(clientBuilder, "enable_typewriter_effect", true, "剧情对话是否使用[打字机效果]");
        TYPEWRITER_EFFECT_SPEED = createInt(clientBuilder, "typewriter_effect_speed", 2, 1, "[打字机效果]打字速度");
        TYPEWRITER_EFFECT_INTERVAL = createInt(clientBuilder, "typewriter_effect_interval", 2, 1, "[打字机效果]打字间隔");
        DIALOG_WIDTH = createInt(clientBuilder, "dialog_width", 300, 100, "对话框宽度");
        SPEC = clientBuilder.build();
    }

    private static ForgeConfigSpec.BooleanValue createBool(ForgeConfigSpec.Builder builder, String key, boolean defaultValue, String... comment) {
        return builder
                .translation("config."+ DialogueLib.MOD_ID+".common."+key)
                .comment(comment)
                .define(key, defaultValue);
    }

    private static ForgeConfigSpec.IntValue createInt(ForgeConfigSpec.Builder builder, String key, int defaultValue, int min, String... comment) {
        return builder
                .translation("config."+ DialogueLib.MOD_ID+".common."+key)
                .comment(comment)
                .defineInRange(key, defaultValue, min, Integer.MAX_VALUE);
    }

    private static ForgeConfigSpec.DoubleValue createDouble(ForgeConfigSpec.Builder builder, String key, double defaultValue, double min, String... comment) {
        return builder
                .translation("config."+ DialogueLib.MOD_ID+".common."+key)
                .comment(comment)
                .defineInRange(key, defaultValue, min, Double.MAX_VALUE);
    }
}
