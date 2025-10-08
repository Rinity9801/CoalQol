package forfun.coalqol.client;

import net.minecraft.client.MinecraftClient;

public class LobbyFinderManager {
    private static boolean enabled = false;
    private static int maxDay = 7;
    private static int delaySeconds = 5;
    private static State currentState = State.DISABLED;
    private static int tickCounter = 0;
    private static long currentDay = 0;

    private enum State {
        DISABLED,
        SENT_WARP,
        WAITING_FOR_DAY_CHECK,
        DELAY_BEFORE_HUB,
        DELAY_AFTER_HUB
    }

    public static void toggle() {
        enabled = !enabled;
        if (enabled) {
            currentState = State.SENT_WARP;
            tickCounter = 0;
            sendCommand("warp crystals");
        } else {
            currentState = State.DISABLED;
            tickCounter = 0;
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void updateConfig(Config config) {
        maxDay = config.lobbyFinderMaxDay;
        delaySeconds = config.lobbyFinderDelay;
    }

    public static void tick() {
        if (!enabled || currentState == State.DISABLED) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world != null) {
            currentDay = client.world.getTimeOfDay() / 24000L;
        }

        tickCounter++;

        switch (currentState) {
            case SENT_WARP:
                if (tickCounter >= 60) {
                    currentState = State.WAITING_FOR_DAY_CHECK;
                    tickCounter = 0;
                }
                break;

            case WAITING_FOR_DAY_CHECK:
                if (currentDay > maxDay) {
                    currentState = State.DELAY_BEFORE_HUB;
                    tickCounter = 0;
                } else {
                    enabled = false;
                    currentState = State.DISABLED;
                    tickCounter = 0;
                }
                break;

            case DELAY_BEFORE_HUB:
                if (tickCounter >= delaySeconds * 20) {
                    sendCommand("hub");
                    currentState = State.DELAY_AFTER_HUB;
                    tickCounter = 0;
                }
                break;

            case DELAY_AFTER_HUB:
                if (tickCounter >= delaySeconds * 20) {
                    sendCommand("warp crystals");
                    currentState = State.SENT_WARP;
                    tickCounter = 0;
                }
                break;
        }
    }

    public static void onIslandMessage() {
        if (!enabled || currentState != State.SENT_WARP) {
            return;
        }

        currentState = State.WAITING_FOR_DAY_CHECK;
        tickCounter = 0;
    }

    public static String getStatusMessage() {
        if (!enabled) {
            return null;
        }

        switch (currentState) {
            case SENT_WARP:
                return "Lobby Finder: Warping...";
            case WAITING_FOR_DAY_CHECK:
                return "Lobby Finder: Checking day... (Day: " + currentDay + "/" + maxDay + ")";
            case DELAY_BEFORE_HUB:
                int beforeHub = (delaySeconds * 20 - tickCounter) / 20;
                return "Lobby Finder: Day too high (" + currentDay + "), hubbing in " + beforeHub + "s";
            case DELAY_AFTER_HUB:
                int afterHub = (delaySeconds * 20 - tickCounter) / 20;
                return "Lobby Finder: Warping in " + afterHub + "s";
            default:
                return "Lobby Finder: Active";
        }
    }

    private static void sendCommand(String command) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.networkHandler.sendCommand(command);
        }
    }
}
