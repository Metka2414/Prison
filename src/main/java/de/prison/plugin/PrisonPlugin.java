package de.prison.plugin;

import de.prison.plugin.commands.PrisonAdminCommand;
import de.prison.plugin.commands.PrisonCommand;
import de.prison.plugin.data.BlockTypeRegistry;
import de.prison.plugin.data.PlayerDataManager;
import de.prison.plugin.gui.PickaxeEnchantGUI;
import de.prison.plugin.gui.PrisonMainGUI;
import de.prison.plugin.listeners.PrisonGUIListener;
import de.prison.plugin.listeners.PrisonMineListener;
import de.prison.plugin.pickaxe.HasteEffectTask;
import de.prison.plugin.pickaxe.PickaxeManager;
import de.prison.plugin.progress.ProgressService;
import de.prison.plugin.world.MineManager;
import de.prison.plugin.world.PrisonWorldGenerator;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class PrisonPlugin extends JavaPlugin {

    private static PrisonPlugin instance;

    private Economy economy;
    private BlockTypeRegistry blockTypeRegistry;
    private PlayerDataManager playerDataManager;
    private PrisonWorldGenerator worldGenerator;
    private MineManager mineManager;
    private PickaxeManager pickaxeManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        setupEconomy();

        blockTypeRegistry = new BlockTypeRegistry();
        playerDataManager = new PlayerDataManager(this);
        worldGenerator = new PrisonWorldGenerator();
        mineManager = new MineManager(worldGenerator);
        pickaxeManager = new PickaxeManager();

        worldGenerator.generateOrLoad(blockTypeRegistry.getFirst());

        ProgressService progressService = new ProgressService(this);

        PrisonMainGUI mainGUI = new PrisonMainGUI(this);
        PickaxeEnchantGUI enchantGUI = new PickaxeEnchantGUI();

        getCommand("prison").setExecutor(new PrisonCommand(this, mainGUI));
        getCommand("prisonadmin").setExecutor(new PrisonAdminCommand(this));

        Bukkit.getPluginManager().registerEvents(new PrisonMineListener(this, progressService), this);
        Bukkit.getPluginManager().registerEvents(new PrisonGUIListener(this, mainGUI, enchantGUI), this);

        HasteEffectTask.start(this, pickaxeManager);

        getLogger().info("PrisonPlugin wurde aktiviert! " + blockTypeRegistry.size() + " Blockarten geladen.");
    }

    @Override
    public void onDisable() {
        if (playerDataManager != null) playerDataManager.save();
        getLogger().info("PrisonPlugin wurde deaktiviert!");
    }

    private void setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            getLogger().warning("Vault nicht gefunden! Geld-Belohnungen sind deaktiviert.");
            return;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp != null) economy = rsp.getProvider();
    }

    public Economy getEconomy() {
        return economy;
    }

    public BlockTypeRegistry getBlockTypeRegistry() {
        return blockTypeRegistry;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public PrisonWorldGenerator getWorldGenerator() {
        return worldGenerator;
    }

    public MineManager getMineManager() {
        return mineManager;
    }

    public PickaxeManager getPickaxeManager() {
        return pickaxeManager;
    }

    public static PrisonPlugin getInstance() {
        return instance;
    }

    public String msg(String path) {
        String prefix = getConfig().getString("messages.prefix", "");
        String message = getConfig().getString("messages." + path, path);
        return color(prefix + message);
    }

    public static String color(String text) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', text);
    }
}
