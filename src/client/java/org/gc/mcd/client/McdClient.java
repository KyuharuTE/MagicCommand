package org.gc.mcd.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.gc.mcd.client.screen.mcdScreen;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.nio.file.Files;
import java.util.logging.Logger;

public class McdClient implements ClientModInitializer {

    private static KeyBinding mKeyBinding;

    @Override
    public void onInitializeClient() {
        if (!new File("./config/mcd.txt").isFile()) {
            Logger.getLogger("MagicCommand").info("MagicCommand: config file not found, creating one...");
            try {
                if (new File("./config/mcd.txt").createNewFile()) {
                    // write default config
                    Files.writeString(new File("./config/mcd.txt").toPath(),
                            "sk-4jPT2mF7DYh4Z5QlvHtr7cFO6JCtDsmenx7jIWUkm8Gj6Jru");
                    Logger.getLogger("MagicCommand").info("MagicCommand: config file created successfully!");
                } else {
                    Logger.getLogger("MagicCommand").warning("MagicCommand: config file creation failed!");
                }
            } catch (Exception e) {
                Logger.getLogger("MagicCommand").warning("error!");
            }
        }

        // Registering the M key binding
        mKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Open Screen",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "MagicCommand"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (mKeyBinding.isPressed()) {
                // 如果屏幕未打开则打开屏幕否则关闭
                if (client.currentScreen == null) {
                    client.setScreen(new mcdScreen());
                } else {
                    client.setScreen(null);
                }
            }
        });
    }
}
