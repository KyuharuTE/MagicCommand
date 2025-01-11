package org.gc.mcd.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.gc.mcd.client.screen.mcdScreen;
import org.lwjgl.glfw.GLFW;

public class McdClient implements ClientModInitializer {

    private static KeyBinding mKeyBinding;

    @Override
    public void onInitializeClient() {
        // Registering the M key binding
        mKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "Open Screen",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_M,
            "MagicCommand"
        ));

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
