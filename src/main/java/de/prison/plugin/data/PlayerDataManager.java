package de.prison.plugin.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private final JavaPlugin plugin;
    private final File file;
    private final Map<UUID, PlayerProgress> progressMap = new HashMap<>();
    private final Map<UUID, Long> shadesMap = new HashMap<>();

    public PlayerDataManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "playerdata.yml");
        load();
    }

    public void load() {
        progressMap.clear();
        shadesMap.clear();
        if (!file.exists()) return;

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (config.getConfigurationSection("players") == null) return;

        for (String uuidStr : config.getConfigurationSection("players").getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                String path = "players." + uuidStr;

                int blockTypeIndex = config.getInt(path + ".blockTypeIndex", 0);
                int stageNumber = config.getInt(path + ".stageNumber", 1);
                int blocksMined = config.getInt(path + ".blocksMined", 0);
                int loopCount = config.getInt(path + ".loopCount", 0);
                long shades = config.getLong(path + ".shades", 0);

                progressMap.put(uuid, new PlayerProgress(blockTypeIndex, stageNumber, blocksMined, loopCount));
                shadesMap.put(uuid, shades);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void save() {
        FileConfiguration config = new YamlConfiguration();
        for (UUID uuid : progressMap.keySet()) {
            PlayerProgress progress = progressMap.get(uuid);
            String path = "players." + uuid;
            config.set(path + ".blockTypeIndex", progress.getBlockTypeIndex());
            config.set(path + ".stageNumber", progress.getStageNumber());
            config.set(path + ".blocksMined", progress.getBlocksMinedInStage());
            config.set(path + ".loopCount", progress.getLoopCount());
            config.set(path + ".shades", shadesMap.getOrDefault(uuid, 0L));
        }
        try {
            if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Konnte playerdata.yml nicht speichern: " + e.getMessage());
        }
    }

    public PlayerProgress getProgress(UUID uuid) {
        return progressMap.computeIfAbsent(uuid, u -> new PlayerProgress());
    }

    public long getShades(UUID uuid) {
        return shadesMap.getOrDefault(uuid, 0L);
    }

    public void addShades(UUID uuid, long amount) {
        shadesMap.put(uuid, getShades(uuid) + amount);
    }

    public boolean removeShadesIfEnough(UUID uuid, long amount) {
        long current = getShades(uuid);
        if (current < amount) return false;
        shadesMap.put(uuid, current - amount);
        return true;
    }

    public void setShades(UUID uuid, long amount) {
        shadesMap.put(uuid, amount);
    }
}
