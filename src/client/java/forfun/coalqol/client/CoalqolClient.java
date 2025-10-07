package forfun.coalqol.client;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import org.lwjgl.glfw.GLFW;

public class CoalqolClient implements ClientModInitializer {

    private static KeyBinding toggleKey;
    private static Config config;

    @Override
    public void onInitializeClient() {
        config = Config.load();
        AutoClickerManager.updateConfig(config);

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.coalqol.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "category.coalqol"
        ));

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("coalqol")
                    .executes(context -> {
                        MinecraftClient client = MinecraftClient.getInstance();
                        client.send(() -> client.setScreen(new ConfigScreen(client.currentScreen, config)));
                        return 1;
                    }));
        });

        HudRenderCallback.EVENT.register(new HudRenderer());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                AutoClickerManager.toggle();
                if (client.player != null) {
                    client.player.sendMessage(
                            net.minecraft.text.Text.literal("Auto-clicker " +
                                    (AutoClickerManager.isEnabled() ? "enabled" : "disabled")),
                            true
                    );
                }
            }

            AutoClickerManager.tick();
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null) {
                AutoClickerManager.cleanup();
            }
        });
    }
}
