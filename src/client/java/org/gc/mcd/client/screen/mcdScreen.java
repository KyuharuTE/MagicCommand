package org.gc.mcd.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.gc.mcd.client.until.aiUntil;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@Environment(EnvType.CLIENT)
public class mcdScreen extends Screen {

    private MultilineText multilineText;

    public mcdScreen() {
        super(Text.of("MagicCommand Screen"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;

        Text text = Text.literal("作者: Kyuharu, B 站: 望风知, Github: https://github.com/KyuharuTE/MagicCommand")
                .formatted(Formatting.WHITE);
        int maxWidth = this.width - 40;
        this.multilineText = MultilineText.create(this.textRenderer, text, maxWidth);

        TextFieldWidget textField = new TextFieldWidget(this.textRenderer, centerX - 100, this.height - 30, 150, 20, Text.of("Input"));
        textField.setPlaceholder(Text.of("请输入提示词..."));
        this.addSelectableChild(textField);

        ButtonWidget sendButton = ButtonWidget.builder(Text.of("发送"), button -> {
            String content = textField.getText().trim();
            if (content.isEmpty()) {
                return;
            }
            button.visible = false;
            executeAiCommand(content, textField, button);
        }).width(50).position(centerX + 60, this.height - 30).build();

        addDrawableChild(textField);
        addDrawableChild(sendButton);
    }

    private void executeAiCommand(String content, TextFieldWidget textField, ButtonWidget button) {
        try {
            String aiResult = aiUntil.getAiReturn(content);
            Logger.getLogger("MagicCommand").info("Get message to server: " + aiResult);
            textField.setText("");
            sendCommandsToServer(aiResult);
        } catch (IOException | InterruptedException | ExecutionException e) {
            Logger.getLogger("MagicCommand").warning("Error sending message to server: " + e.getMessage());
        } finally {
            button.visible = true;
        }
        MinecraftClient.getInstance().setScreen(null);
    }

    private void sendCommandsToServer(String aiResult) {
        if (aiResult.contains("&&")) {
            for (String command : aiResult.split("&&")) {
                sendCommand(command.trim());
            }
        } else {
            sendCommand(aiResult.trim());
        }
    }

    private void sendCommand(String command) {
        if (MinecraftClient.getInstance().player != null) {
            command = command.strip();
            if (command.startsWith("/")) {
                command = command.substring(1);
            }
            MinecraftClient.getInstance().player.networkHandler.sendChatCommand(command);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("MagicCommand"), width / 2, height / 2, 0xffffff);
        this.multilineText.draw(context, width / 2 - this.multilineText.getMaxWidth() / 2, height / 2 + 20, 20, 0xffffff);
    }
}
