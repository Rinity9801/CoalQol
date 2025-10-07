package forfun.coalqol.client;

import forfun.coalqol.client.mixin.PlayerInventoryAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;

public class AutoClickerManager {
    private static boolean enabled = false;
    private static int tickCounter = 0;
    private static int cycleDuration = 102 * 20;
    private static boolean inSequence = false;
    private static int sequenceStep = 0;
    private static int sequenceTickCounter = 0;
    private static boolean firstEnable = true;
    private static int lastSlot = -1;
    private static int sequenceOriginalSlot = 0;
    private static int expectedSlot = 0;
    private static boolean enableRodSwap = true;
    private static boolean enableSecondDrill = false;
    private static int secondDrillSlot = 3;

    public static void toggle() {
        enabled = !enabled;
        if (!enabled) {
            inSequence = false;
            sequenceStep = 0;
            sequenceTickCounter = 0;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null) {
                client.options.attackKey.setPressed(false);
                client.options.useKey.setPressed(false);
            }
        } else if (firstEnable) {
            inSequence = true;
            sequenceStep = 0;
            sequenceTickCounter = 0;
            tickCounter = 0;
            firstEnable = false;
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static int getRemainingTicks() {
        if (firstEnable) {
            return 0;
        }
        return cycleDuration - tickCounter;
    }

    public static int getTotalCycleDuration() {
        return cycleDuration;
    }

    public static void updateConfig(Config config) {
        expectedSlot = config.miningSlot;
        cycleDuration = config.getCooldownTicks();
        enableRodSwap = config.enableRodSwap;
        enableSecondDrill = config.enableSecondDrill;
        secondDrillSlot = config.secondDrillSlot;
    }

    private static int findFishingRodSlot(MinecraftClient client) {
        if (client.player == null) return -1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = client.player.getInventory().getStack(i);
            if (stack.getItem() instanceof FishingRodItem) {
                return i;
            }
        }
        return -1;
    }

    public static void tick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return;
        }

        if (!inSequence) {
            tickCounter++;

            if (tickCounter >= cycleDuration) {
                if (enabled) {
                    inSequence = true;
                    sequenceStep = 0;
                    sequenceTickCounter = 0;
                    tickCounter = 0;
                    sequenceOriginalSlot = ((PlayerInventoryAccessor) client.player.getInventory()).getSelectedSlot();
                }
            }
        }

        if (!enabled) {
            return;
        }

        int currentSlot = ((PlayerInventoryAccessor) client.player.getInventory()).getSelectedSlot();

        if (inSequence) {
            client.options.attackKey.setPressed(false);
            handleManiacMinerSequence(client);
        } else {
            if (currentSlot == expectedSlot) {
                client.options.attackKey.setPressed(true);
            } else {
                client.options.attackKey.setPressed(false);
            }
        }

        lastSlot = currentSlot;
    }

    private static void handleManiacMinerSequence(MinecraftClient client) {
        sequenceTickCounter++;

        switch (sequenceStep) {
            case 0:
                if (enableRodSwap) {
                    int rodSlot = findFishingRodSlot(client);
                    if (rodSlot != -1) {
                        ((PlayerInventoryAccessor) client.player.getInventory()).setSelectedSlot(rodSlot);
                        sequenceStep++;
                        sequenceTickCounter = 0;
                    } else {
                        sequenceStep = 4;
                        sequenceTickCounter = 0;
                    }
                } else {
                    sequenceStep = 4;
                    sequenceTickCounter = 0;
                }
                break;

            case 1:
                if (sequenceTickCounter >= 2) {
                    sequenceStep++;
                    sequenceTickCounter = 0;
                }
                break;

            case 2:
                client.options.useKey.setPressed(true);
                if (sequenceTickCounter >= 3) {
                    client.options.useKey.setPressed(false);
                    sequenceStep++;
                    sequenceTickCounter = 0;
                }
                break;

            case 3:
                if (sequenceTickCounter >= 3) {
                    sequenceStep++;
                    sequenceTickCounter = 0;
                }
                break;

            case 4:
                if (enableSecondDrill) {
                    ((PlayerInventoryAccessor) client.player.getInventory()).setSelectedSlot(secondDrillSlot);
                    sequenceStep++;
                    sequenceTickCounter = 0;
                } else {
                    sequenceStep = 8;
                    sequenceTickCounter = 0;
                }
                break;

            case 5:
                if (sequenceTickCounter >= 2) {
                    sequenceStep++;
                    sequenceTickCounter = 0;
                }
                break;

            case 6:
                client.options.useKey.setPressed(true);
                if (sequenceTickCounter >= 3) {
                    client.options.useKey.setPressed(false);
                    sequenceStep++;
                    sequenceTickCounter = 0;
                }
                break;

            case 7:
                if (sequenceTickCounter >= 3) {
                    sequenceStep++;
                    sequenceTickCounter = 0;
                }
                break;

            case 8:
                ((PlayerInventoryAccessor) client.player.getInventory()).setSelectedSlot(expectedSlot);
                sequenceStep++;
                sequenceTickCounter = 0;
                break;

            case 9:
                if (sequenceTickCounter >= 3) {
                    sequenceStep++;
                    sequenceTickCounter = 0;
                }
                break;

            case 10:
                client.options.useKey.setPressed(true);
                if (sequenceTickCounter >= 3) {
                    client.options.useKey.setPressed(false);
                    sequenceStep++;
                    sequenceTickCounter = 0;
                }
                break;

            case 11:
                inSequence = false;
                sequenceStep = 0;
                sequenceTickCounter = 0;
                tickCounter = 0;
                break;
        }
    }

    public static void cleanup() {
        if (enabled) {
            MinecraftClient client = MinecraftClient.getInstance();
            client.options.attackKey.setPressed(false);
            client.options.useKey.setPressed(false);
        }
    }
}
