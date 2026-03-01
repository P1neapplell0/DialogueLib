package com.p1nero.dialog_lib.client.screen;

import com.p1nero.dialog_lib.DialogueLibConfig;
import com.p1nero.dialog_lib.client.screen.component.DialogueAnswerComponent;
import com.p1nero.dialog_lib.client.screen.component.DialogueOptionComponent;
import com.p1nero.dialog_lib.network.DialoguePacketHandler;
import com.p1nero.dialog_lib.network.DialoguePacketRelay;
import com.p1nero.dialog_lib.network.packet.serverbound.HandleCustomInteractPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DialogueScreen extends Screen {
    protected String modId;
    protected ResourceLocation id;
    protected ResourceLocation pictureLocation = null;
    public static final int BACKGROUND_COLOR = 0xCC000000;
    public static final int BORDER_COLOR = 0xFFFFFFFF;
    protected int picHeight = 144, picWidth = 256;
    protected int picShowHeight = 144, picShowWidth = 256;
    protected int yOffset = 0;
    protected int optionXOffset = 0;
    protected float rate;
    protected boolean isSilent;
    protected int currentOptionsCount;
    protected DialogueAnswerComponent dialogueAnswer;
    public final int typewriterInterval;
    protected int typewriterTimer = 0;

    public DialogueScreen(String modId) {
        super(Component.empty());
        this.modId = modId;
        typewriterInterval = DialogueLibConfig.TYPEWRITER_EFFECT_INTERVAL.get();
        this.dialogueAnswer = new DialogueAnswerComponent(Component.empty());
    }

    public void setCustomTitle(Component customTitle) {
        this.dialogueAnswer = new DialogueAnswerComponent(customTitle);
    }

    public void setModId(String modId) {
        this.modId = modId;
    }

    public void setId(ResourceLocation id) {
        this.id = id;
    }

    /**
     * 默认名字风格
     */
    public MutableComponent buildDialogueAnswerName(Component component) {
        return Component.literal("[").append(component.copy().withStyle(ChatFormatting.GOLD)).append("]");
    }

    @Override
    protected void init() {
        positionDialogue();
    }

    public void setPicture(ResourceLocation resourceLocation) {
        this.pictureLocation = resourceLocation;
        if (Minecraft.getInstance().level != null && Minecraft.getInstance().player != null) {
            LocalPlayer player = Minecraft.getInstance().player;
            Minecraft.getInstance().level.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1.0F, 1.0F, false);
        }
    }

    public void setSilent(boolean silent) {
        isSilent = silent;
    }

    public void setYOffset(int yOffset) {
        this.yOffset = yOffset;
    }

    public void setOptionXOffset(int optionXOffset) {
        this.optionXOffset = optionXOffset;
    }

    public void setPicHeight(int picHeight) {
        this.picHeight = picHeight;
    }

    public void setPicWidth(int picWidth) {
        this.picWidth = picWidth;
    }

    public void setPicShowHeight(int picShowHeight) {
        this.picShowHeight = picShowHeight;
    }

    public void setPicShowWidth(int picShowWidth) {
        this.picShowWidth = picShowWidth;
    }

    public void setupDialogueOptions(List<DialogueOptionComponent> options) {
        currentOptionsCount = options.size();
        this.clearWidgets();
        for (DialogueOptionComponent option : options) {
            this.addRenderableWidget(option);
        }
        this.positionDialogue();
        this.resetOffset();
    }

    public void resetOffset() {
        yOffset = 0;
        optionXOffset = 0;
    }

    public void reset() {
        pictureLocation = null;
        yOffset = 0;
        optionXOffset = 0;
        picHeight = 144;
        picWidth = 256;
        picShowHeight = 256;
        picShowWidth = 144;
    }

    /**
     * 矫正位置，两种风格
     */
    protected void positionDialogue() {
        if (DialogueLibConfig.OPTION_IN_CENTER.get()) {
            positionDialogue1();
        } else {
            positionDialogue2();
        }
    }

    /**
     * 按钮居中的风格
     */
    protected void positionDialogue1() {
        rate = 5.0F / 4;
        // Dialogue answer.
        this.dialogueAnswer.reposition(this.width, (int) (this.height * rate), yOffset);
        // Dialogue choices.
        int lineNumber = this.dialogueAnswer.height / 12 + 1;
        Iterator<Renderable> iterator = this.renderables.iterator();
        while (iterator.hasNext()) {
            Renderable renderable = iterator.next();
            if (renderable instanceof DialogueOptionComponent option) {
                option.setX(this.width / 2 - option.getWidth() / 2);
                int y = (int) (this.height / 2.0 * rate + 12 * lineNumber + yOffset);
                option.setY(y);
                lineNumber++;
                int h = option.getHeight() + 2;
                if (!iterator.hasNext() && y + h > this.height && typewriterTimer < 0) {
                    //防止超过屏幕
                    yOffset -= 1;
                    this.dialogueAnswer.reposition(this.width, (int) (this.height * rate), yOffset);
                    y = (int) (this.height / 2.0 * rate + 12 * lineNumber + yOffset);
                    option.setY(y);//调低一点
                }
            }
        }
    }

    /**
     * 按钮右置的风格
     */
    protected void positionDialogue2() {
        rate = 1.4F;
        this.dialogueAnswer.reposition(this.width, (int) (this.height * rate), yOffset);
        int answerBottomY = this.dialogueAnswer.getStartY() + this.dialogueAnswer.height;
        //防止底下超过屏幕
        if(answerBottomY + 10 > this.height && typewriterTimer < 0){
            yOffset -= 1;
        }

        int lineNumber = 0;
        for (Renderable renderable : this.renderables) {
            if (renderable instanceof DialogueOptionComponent option) {
                //防止选项右边超过屏幕
                int x = this.width / 2 + this.width / 6;
                if(x + option.getWidth() + 2 + optionXOffset > this.width) {
                    optionXOffset = (this.width - (x + option.getWidth() + 2));
                }
                option.setX(x + optionXOffset);
                int y = (int) (this.height / 2.0 * rate + 12 * lineNumber + yOffset - currentOptionsCount * 12);
                option.setY(y);
                lineNumber++;
            }
        }
    }

    public void setDialogueAnswer(Component component) {
        if (DialogueLibConfig.ENABLE_TYPEWRITER_EFFECT.get()) {
            this.dialogueAnswer.updateTypewriterDialogue(component);
        } else {
            this.dialogueAnswer.updateDialogue(component);
        }

    }

    public void finishChat(int interactionID) {
        sendPacket(interactionID);
        reset();
        super.onClose();
    }

    /**
     * 默认对话翻页的时候播放声音
     */
    protected void playSound() {

    }

    /**
     * 发包但不关闭窗口
     */
    public void sendPacket(int interactionID) {
        DialoguePacketRelay.sendToServer(DialoguePacketHandler.INSTANCE, new HandleCustomInteractPacket(id, interactionID));
    }

    /**
     * 单机后可提前显示
     */
    @Override
    public boolean mouseClicked(double v, double v1, int i) {
        if (this.dialogueAnswer.index < dialogueAnswer.max - 3) {
            this.dialogueAnswer.index = dialogueAnswer.max - 3;
        }
        return super.mouseClicked(v, v1, i);
    }

    /**
     * 检查回答是否全部可见
     */
    public boolean shouldRenderOption(){
        if(DialogueLibConfig.ENABLE_TYPEWRITER_EFFECT.get()) {
            return this.dialogueAnswer.shouldRenderOption();
        }
        return true;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        this.renderPicture(guiGraphics);
        //每tick显示打字机效果太慢了= =
        if (DialogueLibConfig.ENABLE_TYPEWRITER_EFFECT.get() && typewriterTimer < 0) {
            this.dialogueAnswer.updateTypewriterDialogue();
            positionDialogue();
            typewriterTimer = typewriterInterval;
        } else {
            typewriterTimer--;
        }

        this.dialogueAnswer.render(guiGraphics);

        //如果回答还没显示完则不渲染选项
        for (Renderable renderable : this.renderables) {
            if (renderable instanceof DialogueOptionComponent && !dialogueAnswer.shouldRenderOption()) {
                continue;
            }
            renderable.render(guiGraphics, mouseX, mouseY, partialTicks);
        }
    }

    protected void renderPicture(GuiGraphics guiGraphics) {
        if (pictureLocation != null) {
            guiGraphics.blit(pictureLocation, this.width / 2 - picShowWidth / 2, (int) ((float) this.height / 2 - picShowHeight / 1.3F), picShowWidth, picShowHeight, 0, 0, picWidth, picHeight, picWidth, picHeight);
        }
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics) {
        if(DialogueLibConfig.ENABLE_BACKGROUND.get()) {
            int posY = (int) (this.height / 2.0 * rate + yOffset - 5);
            if(DialogueLibConfig.FADED_BACKGROUND.get()) {
                //渐变覆盖全部屏幕的黑底背景（性能耗费有点大hhh）
                int gradientHeight = this.height - posY;
                for (int i = 0; i < gradientHeight; i++) {
                    float progress = (float) i / gradientHeight;
                    float curve = progress * progress;
                    int alpha = (int) (0xA0 * (1 - curve));
                    int color = (alpha << 24);
                    if (alpha > 0) {
                        int currentY = this.height - i;
                        guiGraphics.fill(0, currentY, this.width, currentY + 1, color);
                    }
                }
            } else {
                //普通边框背景
                int tooltipHeight = dialogueAnswer.height + 10;
                if(DialogueLibConfig.OPTION_IN_CENTER.get()) {
                    tooltipHeight += (currentOptionsCount + 1) * 12;
                }
                int tooltipWidth = DialogueLibConfig.DIALOG_WIDTH.get() + 40;
                int posX = this.width / 2 - tooltipWidth / 2;
                guiGraphics.fill(posX, posY, posX + tooltipWidth, posY + tooltipHeight, BACKGROUND_COLOR);
                guiGraphics.renderOutline(posX, posY, tooltipWidth, tooltipHeight, BORDER_COLOR);
            }
        }
    }

    @Override
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        this.width = width;
        this.height = height;
        this.positionDialogue();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        this.finishChat(0);
    }

}