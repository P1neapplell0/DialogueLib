package com.p1nero.dialog_lib.api.component;

import com.p1nero.dialog_lib.client.screen.DialogueScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DialogNode {

    public static final int NOT_EXECUTE = -114514;

    protected Component answer;
    protected Component option = Component.empty();

    @Nullable
    protected Consumer<DialogueScreen> screenConsumer;//要执行的操作

    public int getExecuteValue() {
        return executeValue;
    }

    protected int executeValue = NOT_EXECUTE;//要执行的操作代码 ，114514 代表无操作

    protected List<DialogNode> options = new ArrayList<>();

    /**
     * 根节点不应该有选项。
     *
     */
    public DialogNode(Component answer) {
        this.answer = answer;
    }

    public DialogNode(Component answer, Component option) {
        this.answer = answer;
        this.option = option;
    }

    public DialogNode(Component answer, Component option, Consumer<DialogueScreen> screenConsumer) {
        this(answer, option, NOT_EXECUTE, screenConsumer);
    }

    public DialogNode(Component answer, Component option, int executeValue) {
        this(answer, option, executeValue, null);
    }

    public DialogNode(Component answer, Component option, int executeValue, Consumer<DialogueScreen> screenConsumer) {
        this.answer = answer;
        this.option = option;
        this.executeValue = executeValue;
        this.screenConsumer = screenConsumer;
    }

    public DialogNode addLeaf(Component option, int returnValue) {
        options.add(new FinalNode(option, returnValue));
        return this;
    }

    /**
     * 默认的情况。0不会被处理
     */
    public DialogNode addLeaf(Component option) {
        options.add(new FinalNode(option, 0));
        return this;
    }

    public DialogNode addChild(Component answer, Component option, int executeValue, Consumer<DialogueScreen> execute) {
        DialogNode node = new DialogNode(answer, option);
        node.addExecutable(execute);
        node.addExecutable(executeValue);
        options.add(node);
        return this;
    }

    public DialogNode addChild(Component answer, Component option, Consumer<DialogueScreen> execute) {
        DialogNode node = new DialogNode(answer, option);
        node.addExecutable(execute);
        options.add(node);
        return this;
    }

    public DialogNode addChild(Component answer, Component option, int executeValue) {
        DialogNode node = new DialogNode(answer, option);
        node.addExecutable(executeValue);
        options.add(node);
        return this;
    }

    public DialogNode addChild(Component answer, Component option) {
        options.add(new DialogNode(answer, option));
        return this;
    }

    public DialogNode addChild(DialogNode node) {
        options.add(node);
        return this;
    }

    public DialogNode foreachAdd(DialogNode node) {
        options.forEach(dialogNode -> dialogNode.addChild(node));
        return this;
    }

    public DialogNode addExecutable(Consumer<DialogueScreen> runnable) {
        this.screenConsumer = runnable;
        return this;
    }

    public DialogNode addExecutable(int executeValue) {
        this.executeValue = executeValue;
        return this;
    }

    public void execute(DialogueScreen screen) {
        if (screenConsumer != null) {
            screenConsumer.accept(screen);
        }
    }

    public boolean canExecute() {
        return screenConsumer != null;
    }

    public boolean canExecuteCode() {
        return executeValue != -114514;
    }

    public Component getAnswer() {
        return answer;
    }

    public Component getOption() {
        return option;
    }

    public List<DialogNode> getChildren() {
        return options;
    }

    public static class FinalNode extends DialogNode {
        private final int returnValue;

        public FinalNode(Component finalOption, int returnValue) {
            super(Component.empty());//最终节点不需要回答
            this.option = finalOption;
            this.returnValue = returnValue;
        }

        public FinalNode(Component finalOption, int returnValue, Consumer<DialogueScreen> consumer) {
            this(finalOption, returnValue);
            this.screenConsumer = consumer;
        }

        public int getReturnValue() {
            return returnValue;
        }

    }

}

