package forfun.coalqol.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class HudRenderer implements HudRenderCallback {
    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) {
            return;
        }

        int remainingTicks = AutoClickerManager.getRemainingTicks();
        int totalTicks = AutoClickerManager.getTotalCycleDuration();

        String timerText;

        if (remainingTicks <= 0) {
            timerText = "READY";
        } else {
            int remainingSeconds = remainingTicks / 20;
            int minutes = remainingSeconds / 60;
            int seconds = remainingSeconds % 60;
            timerText = String.format("%d:%02d", minutes, seconds);
        }

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        int x = screenWidth / 2;
        int y = screenHeight / 2 + 20;

        int textWidth = client.textRenderer.getWidth(timerText);
        drawContext.drawText(
            client.textRenderer,
            timerText,
            x - textWidth / 2,
            y,
            0xFFFFFF,
            true
        );

        String lobbyFinderStatus = LobbyFinderManager.getStatusMessage();
        if (lobbyFinderStatus != null) {
            int statusWidth = client.textRenderer.getWidth(lobbyFinderStatus);
            drawContext.drawText(
                client.textRenderer,
                lobbyFinderStatus,
                x - statusWidth / 2,
                y + 15,
                0x00FF00,
                true
            );
        }
    }
}
