package forfun.coalqol.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private final Config config;
    private SliderWidget miningSlotSlider;
    private TextFieldWidget cooldownInput;
    private ButtonWidget rodSwapToggle;
    private ButtonWidget secondDrillToggle;
    private SliderWidget secondDrillSlotSlider;

    public ConfigScreen(Screen parent, Config config) {
        super(Text.literal("Coal QOL Settings"));
        this.parent = parent;
        this.config = config;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = 40;
        this.miningSlotSlider = new SliderWidget(
                centerX - 150,
                startY,
                300,
                20,
                Text.literal("Mining Slot: " + (config.miningSlot + 1)),
                config.miningSlot / 8.0
        ) {
            @Override
            protected void updateMessage() {
                int slot = (int) Math.round(this.value * 8);
                this.setMessage(Text.literal("Mining Slot: " + (slot + 1)));
            }

            @Override
            protected void applyValue() {
                config.miningSlot = (int) Math.round(this.value * 8);
            }
        };
        this.addDrawableChild(this.miningSlotSlider);

        this.cooldownInput = new TextFieldWidget(
                this.textRenderer,
                centerX - 150,
                startY + 50,
                300,
                20,
                Text.literal("Maniac Miner Cooldown")
        );
        this.cooldownInput.setMaxLength(4);
        this.cooldownInput.setText(String.valueOf(config.maniacMinerCooldown));
        this.cooldownInput.setChangedListener(text -> {
            try {
                int value = Integer.parseInt(text);
                if (value > 0 && value <= 9999) {
                    config.maniacMinerCooldown = value;
                }
            } catch (NumberFormatException ignored) {
            }
        });
        this.addDrawableChild(this.cooldownInput);

        this.rodSwapToggle = ButtonWidget.builder(
                Text.literal("Rod Swap: " + (config.enableRodSwap ? "ON" : "OFF")),
                button -> {
                    config.enableRodSwap = !config.enableRodSwap;
                    button.setMessage(Text.literal("Rod Swap: " + (config.enableRodSwap ? "ON" : "OFF")));
                }
        ).dimensions(centerX - 150, startY + 100, 300, 20).build();
        this.addDrawableChild(this.rodSwapToggle);

        this.secondDrillToggle = ButtonWidget.builder(
                Text.literal("Second Drill: " + (config.enableSecondDrill ? "ON" : "OFF")),
                button -> {
                    config.enableSecondDrill = !config.enableSecondDrill;
                    button.setMessage(Text.literal("Second Drill: " + (config.enableSecondDrill ? "ON" : "OFF")));
                    this.secondDrillSlotSlider.active = config.enableSecondDrill;
                }
        ).dimensions(centerX - 150, startY + 150, 300, 20).build();
        this.addDrawableChild(this.secondDrillToggle);

        this.secondDrillSlotSlider = new SliderWidget(
                centerX - 150,
                startY + 200,
                300,
                20,
                Text.literal("Second Drill Slot: " + (config.secondDrillSlot + 1)),
                config.secondDrillSlot / 8.0
        ) {
            @Override
            protected void updateMessage() {
                int slot = (int) Math.round(this.value * 8);
                this.setMessage(Text.literal("Second Drill Slot: " + (slot + 1)));
            }

            @Override
            protected void applyValue() {
                config.secondDrillSlot = (int) Math.round(this.value * 8);
            }
        };
        this.secondDrillSlotSlider.active = config.enableSecondDrill;
        this.addDrawableChild(this.secondDrillSlotSlider);

        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> {
            config.save();
            AutoClickerManager.updateConfig(config);
            this.close();
        }).dimensions(centerX - 100, startY + 250, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fillGradient(0, 0, this.width, this.height, 0xC0101010, 0xD0101010);

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                this.title,
                this.width / 2,
                20,
                0xFFFFFF
        );

        int centerX = this.width / 2;
        int startY = 40;

        context.drawTextWithShadow(
                this.textRenderer,
                Text.literal("Select which hotbar slot to auto-mine on:"),
                centerX - 150,
                startY - 15,
                0xAAAAAA
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.literal("Maniac Miner Cooldown (seconds):"),
                centerX - 150,
                startY + 35,
                0xAAAAAA
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.literal("Auto-detect fishing rod and swap:"),
                centerX - 150,
                startY + 85,
                0xAAAAAA
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.literal("Enable second drill swap:"),
                centerX - 150,
                startY + 135,
                0xAAAAAA
        );

        context.drawTextWithShadow(
                this.textRenderer,
                Text.literal("Second drill hotbar slot:"),
                centerX - 150,
                startY + 185,
                config.enableSecondDrill ? 0xAAAAAA : 0x666666
        );

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }
}
