package org.gc.mcd.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.gc.mcd.client.until.aiUntil;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@Environment(EnvType.CLIENT)
public class mcdScreen extends Screen {

    public mcdScreen() {
        super(Text.of("MagicCommand Screen"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;

        TextFieldWidget textField = new TextFieldWidget(this.textRenderer, centerX - 100, this.height - 30, 150, 20, Text.of("Input"));
        textField.setPlaceholder(Text.of("请输入提示词..."));
        this.addSelectableChild(textField);

        ButtonWidget sendButton = ButtonWidget.builder(Text.of("发送"), button -> {
            String content = textField.getText().trim();
            if (content.isEmpty()) {
                return;
            }
            button.visible = false;
            try {
                String aiResult = aiUntil.getAiReturn(content);
                Logger.getLogger("MagicCommand").info("Get message to server: " + aiResult);
                textField.setText("");
                if (MinecraftClient.getInstance().player != null) {
                    MinecraftClient.getInstance().player.networkHandler.sendChatCommand(aiResult.startsWith("/") ? aiResult.substring(1) : aiResult);
                }
            } catch (IOException | InterruptedException | ExecutionException e) {
                Logger.getLogger("MagicCommand").warning("Error sending message to server: " + e.getMessage());
            } finally {
                button.visible = true;
            }
            MinecraftClient.getInstance().setScreen(null);
        }).width(50).position(centerX + 60, this.height - 30).build();

        addDrawableChild(textField);
        addDrawableChild(sendButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("MagicCommand"), width / 2, height / 2, 0xffffff);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("作者: Kyuharu, B 站: 望风知"), width / 2, height / 2 + 30, 0xffffff);
    }
}
