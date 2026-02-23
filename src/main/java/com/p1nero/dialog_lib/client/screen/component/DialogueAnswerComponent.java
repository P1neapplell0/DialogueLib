package com.p1nero.dialog_lib.client.screen.component;

import com.p1nero.dialog_lib.DialogueLibConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public class DialogueAnswerComponent {
    //分割后的对话
    private final List<NpcDialogueElement> splitLines;
    //完整的分割后的对话
    private final List<NpcDialogueElement> fullSplitLines;
    private Component message;
    private final Component name;
    public int height;
    //打字机效果的下标
    public int index;
    //打字机效果的最大值
    public int max;
    public int maxWidth;

    private boolean shouldRenderOption = false;

    public DialogueAnswerComponent(Component name) {
        this.splitLines = new ArrayList<>();
        this.fullSplitLines = new ArrayList<>();
        this.name = name;
        this.updateDialogue(Component.empty());
    }

    public boolean shouldRenderOption() {
        if (!DialogueLibConfig.ENABLE_TYPEWRITER_EFFECT.get()) {
            return true;
        }
        return shouldRenderOption;
    }

    public Component getMessage() {
        return message;
    }

    public void render(GuiGraphics guiGraphics) {
        this.splitLines.forEach(element -> element.render(guiGraphics));
    }

    /**
     * 如果启动打字机效果，则所有文本按完整文本第一行出现的文本的最左侧定位。因为这样阅读比较舒服
     * 先保存一份完整文本，在沿用天堂的计算定位
     */
    public void reposition(int width, int height, int yOffset) {
        for (int i = 0, j = 0; i < splitLines.size(); i++) {
            NpcDialogueElement dialogue = splitLines.get(i);
            dialogue.width = Minecraft.getInstance().font.width(dialogue.text) + 2;

            if (DialogueLibConfig.ENABLE_TYPEWRITER_EFFECT.get() && i != 0) {//因为第一个变量是NPC名字，所以要取下标1。
                dialogue.x = width / 2 - maxWidth / 2;
            } else {
                dialogue.x = width / 2 - dialogue.width / 2;
            }
            dialogue.y = height / 2 + j * 12 + yOffset;
            j++;
        }
        this.height = this.splitLines.size() * 12;
    }

    public int getStartY() {
        if(this.splitLines.isEmpty()) {
            return 0;
        }
        return splitLines.get(0).y;
    }

    public void updateDialogue(Component message) {
        updateSplitLines(splitLines, message);
    }

    private void updateSplitLines(List<NpcDialogueElement> pSplitLine, Component message) {
        pSplitLine.clear();
        List<FormattedCharSequence> list = Minecraft.getInstance().font.split(name.copy().append(message), DialogueLibConfig.DIALOG_WIDTH.get());
        this.height = list.size() * 12;
        list.forEach(text -> pSplitLine.add(new NpcDialogueElement(0, 0, 0, text)));
    }

    /**
     * 更新打字机效果的完整文本内容，并且执行一次打印机效果。
     */
    public void updateTypewriterDialogue(Component message) {
        this.message = message;
        updateSplitLines(fullSplitLines, message);

        //以最长那句话的最左边为最左边。
        maxWidth = 0;
        for (NpcDialogueElement element : fullSplitLines) {
            maxWidth = Math.max(Minecraft.getInstance().font.width(element.text) + 2, maxWidth);
        }

        shouldRenderOption = false;
        index = 0;
        max = message.getString().length();
        updateTypewriterDialogue();
    }

    /**
     * 添加打字机效果，一次更新一个字
     */
    public void updateTypewriterDialogue() {
        Style style = message.getStyle();
        updateDialogue(Component.literal(message.getString(index)).withStyle(style));
        index += DialogueLibConfig.TYPEWRITER_EFFECT_SPEED.get();
        if (index > max) {
            index = max;
            shouldRenderOption = true;
        }
    }

    /**
     * This inner class is used to store data for each line of text.
     */
    public static class NpcDialogueElement {
        private final FormattedCharSequence text;
        private int x;
        private int y;
        private int width;

        public NpcDialogueElement(int x, int y, int width, FormattedCharSequence text) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.width = width;
        }

        public void render(GuiGraphics guiGraphics) {
            if (DialogueLibConfig.ENABLE_ANS_BACKGROUND.get()) {
                guiGraphics.fillGradient(this.x, this.y, this.x + width, this.y + 12, 0x66000000, 0x66000000);
            }
            guiGraphics.drawString(Minecraft.getInstance().font, this.text, this.x + 1, this.y + 1, 0xFFFFFF);
        }
    }
}
