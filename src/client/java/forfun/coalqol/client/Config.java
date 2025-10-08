package forfun.coalqol.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("coal-qol.json");

    public int miningSlot = 0;
    public int maniacMinerCooldown = 102;
    public boolean enableRodSwap = true;
    public boolean enableSecondDrill = false;
    public int secondDrillSlot = 3;
    public int lobbyFinderMaxDay = 7;
    public int lobbyFinderDelay = 5;

    public static Config load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                return GSON.fromJson(json, Config.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new Config();
    }

    public void save() {
        try {
            String json = GSON.toJson(this);
            Files.writeString(CONFIG_PATH, json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getCooldownTicks() {
        return maniacMinerCooldown * 20;
    }
}
