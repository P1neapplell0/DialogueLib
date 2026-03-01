package com.p1nero.dialog_lib.client.screen.builder;

import com.p1nero.dialog_lib.api.component.DialogNode;
import com.p1nero.dialog_lib.api.entity.custom.IEntityNpc;
import com.p1nero.dialog_lib.client.screen.DialogueScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Consumer;

/**
 * 流式构造对话
 */
public class StreamDialogueScreenBuilder extends DialogueScreenBuilder {

    public StreamDialogueScreenBuilder(Component defaultTitle, ResourceLocation id) {
        super(defaultTitle, id);
    }

    public StreamDialogueScreenBuilder(BlockState blockState, BlockPos pos, String modId) {
        super(blockState, pos, modId);
    }

    public StreamDialogueScreenBuilder(Entity entity, String modId) {
        super(entity, modId);
    }

    /**
     * 初始化对话框，得先start才能做后面的操作
     */
    public StreamDialogueScreenBuilder start(Component greeting, int executeValue, Consumer<DialogueScreen> screenConsumer) {
        root = new DialogNode(greeting, Component.empty(), executeValue, screenConsumer);
        tempNode = root;
        return this;
    }

    public StreamDialogueScreenBuilder start(Component greeting, Consumer<DialogueScreen> screenConsumer) {
        return start(greeting, DialogNode.NOT_EXECUTE, screenConsumer);
    }

    public StreamDialogueScreenBuilder start(Component greeting, int executeValue) {
        return start(greeting, executeValue, null);
    }

    public StreamDialogueScreenBuilder start(Component greeting) {
        root = new DialogNode(greeting);
        tempNode = root;
        return this;
    }

    /**
     * 初始化对话框，得先start才能做后面的操作
     *
     * @param greeting 初始时显示的话的编号
     */
    public StreamDialogueScreenBuilder start(int greeting) {
        return start(componentBuilder.ans(greeting));
    }

    public StreamDialogueScreenBuilder start(int greeting, int executeValue) {
        return start(componentBuilder.ans(greeting), executeValue);
    }

    public StreamDialogueScreenBuilder start(int greeting, Consumer<DialogueScreen> screenConsumer) {
        return start(componentBuilder.ans(greeting), screenConsumer);
    }

    public StreamDialogueScreenBuilder start(int greeting, int executeValue, Consumer<DialogueScreen> screenConsumer) {
        return start(componentBuilder.ans(greeting), executeValue, screenConsumer);
    }

    /**
     * @param finalOption 最后显示的话
     * @param returnValue 选项的返回值，默认返回0。用于处理 {@link IEntityNpc#handleNpcInteraction(ServerPlayer, int)}
     */
    public StreamDialogueScreenBuilder addFinalOption(Component finalOption, int returnValue) {
        return addFinalOption(finalOption, returnValue, null);
    }

    public StreamDialogueScreenBuilder addFinalOption(Component finalOption, int returnValue, Consumer<DialogueScreen> screenConsumer) {
        if (tempNode == null)
            return null;
        tempNode.addChild(new DialogNode.FinalNode(finalOption, returnValue, screenConsumer));
        return this;
    }

    public StreamDialogueScreenBuilder addFinalOption(Component finalOption, Consumer<DialogueScreen> screenConsumer) {
        if (tempNode == null)
            return null;
        tempNode.addChild(new DialogNode.FinalNode(finalOption, 0, screenConsumer));
        return this;
    }

    public StreamDialogueScreenBuilder addFinalOption(Component finalOption) {
        return addFinalOption(finalOption, 0);
    }

    /**
     * @param finalOption 最后显示的话
     * @param returnValue 选项的返回值，默认返回0。用于处理 {@link IEntityNpc#handleNpcInteraction(ServerPlayer, int)}
     */
    public StreamDialogueScreenBuilder addFinalOption(int finalOption, int returnValue) {
        return addFinalOption(componentBuilder.opt(finalOption), returnValue);
    }

    public StreamDialogueScreenBuilder addFinalOption(int finalOption, int returnValue, Consumer<DialogueScreen> screenConsumer) {
        return addFinalOption(componentBuilder.opt(finalOption), returnValue, screenConsumer);
    }

    public StreamDialogueScreenBuilder addFinalOption(int finalOption, Consumer<DialogueScreen> screenConsumer) {
        return addFinalOption(componentBuilder.opt(finalOption), screenConsumer);
    }

    public StreamDialogueScreenBuilder addFinalOption(int finalOption) {
        return addFinalOption(finalOption, 0);
    }

    /**
     * 添加选项进树并返回下一个节点
     *
     * @param option 该选项的内容
     * @param answer 选择该选项后的回答内容
     */
    public StreamDialogueScreenBuilder addOption(Component option, Component answer, int executeValue, Consumer<DialogueScreen> screenConsumer) {
        if (tempNode == null)
            return null;
        tempNode.addChild(answer, option, executeValue, screenConsumer);

        //直接下一个
        List<DialogNode> list = tempNode.getChildren();
        if (!(list.size() == 1 && list.get(0) instanceof DialogNode.FinalNode)) {
            tempNode = list.get(0);
        }

        return this;
    }

    public StreamDialogueScreenBuilder addOption(Component option, Component answer) {
        return addOption(option, answer, DialogNode.NOT_EXECUTE, null);
    }

    public StreamDialogueScreenBuilder addOption(Component option, Component answer, Consumer<DialogueScreen> screenConsumer) {
        return addOption(option, answer, DialogNode.NOT_EXECUTE, screenConsumer);
    }

    public StreamDialogueScreenBuilder addOption(Component option, Component answer, int executeValue) {
        return addOption(option, answer, executeValue, null);
    }

    /**
     * 辅助构建 实体id + mod id + 编号的翻译键
     */
    public StreamDialogueScreenBuilder addOption(int option, int answer, int executeValue, Consumer<DialogueScreen> screenConsumer) {
        return addOption(componentBuilder.opt(option), componentBuilder.ans(answer), executeValue, screenConsumer);
    }

    public StreamDialogueScreenBuilder addOption(int option, int answer, Consumer<DialogueScreen> screenConsumer) {
        return addOption(option, answer, DialogNode.NOT_EXECUTE, screenConsumer);
    }

    public StreamDialogueScreenBuilder addOption(int option, int answer, int executeValue) {
        return addOption(option, answer, executeValue, null);
    }

    public StreamDialogueScreenBuilder addOption(int option, int answer) {
        return addOption(option, answer, DialogNode.NOT_EXECUTE, null);
    }

    /**
     * 按下按钮后执行
     */
    public StreamDialogueScreenBuilder thenExecute(Consumer<DialogueScreen> consumer) {
        if (tempNode == null)
            return null;
        tempNode.addExecutable(consumer);
        return this;
    }

    public StreamDialogueScreenBuilder thenExecute(int returnValue) {
        tempNode.addExecutable(returnValue);
        return this;
    }

}
