package org.gc.mcd.client.screen;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class mcdKeyScreen extends Screen {

    public mcdKeyScreen() {
        super(Text.of("MagicCommand Screen"));
    }

    @Override
    public void close() {
        client.setScreen(new mcdScreen());
    }

    @Override
    protected void init() {
        // int centerX = width / 2;
        // int centerY = height / 2;
        TextFieldWidget textField = new TextFieldWidget(textRenderer, (this.width - (200 + 50 + 10)) / 2,
                (this.height - 20) / 2, 200, 20,
                Text.of("Input"));
        textField.setPlaceholder(Text.of("请填写 Key (ChatGPT)"));
        textField.setMaxLength(255);
        ButtonWidget sendButton = ButtonWidget.builder(Text.of("修改"), button -> {
            String content = textField.getText();
            if (content != null && !content.isEmpty()) {
                if (!new File("./config/mcd.txt").isFile()) {
                    try {
                        new File("./config/mcd.txt").createNewFile();
                    } catch (IOException e) {
                        MinecraftClient.getInstance().player.networkHandler.sendChatCommand("say [Error]创建文件失败");
                        client.setScreen(null);
                        e.printStackTrace();
                    }
                }

                try {
                    Files.writeString(new File("./config/mcd.txt").toPath(), content);
                } catch (IOException e) {
                    MinecraftClient.getInstance().player.networkHandler.sendChatCommand("say [Error]写入文件失败");
                    client.setScreen(null);
                    e.printStackTrace();
                }

                client.setScreen(new mcdScreen());
            }
        }).width(50).position((this.width - (200 + 50 + 10)) / 2 + 200 + 10, (this.height - 20) / 2).build();

        addDrawableChild(textField);
        addDrawableChild(sendButton);
    }

}
