package de.prison.plugin.progress;

import de.prison.plugin.PrisonPlugin;
import de.prison.plugin.data.BlockTypeRegistry;
import de.prison.plugin.data.PlayerDataManager;
import de.prison.plugin.data.PlayerProgress;
import de.prison.plugin.data.PrisonBlockType;
import de.prison.plugin.data.PrisonStage;
import de.prison.plugin.pickaxe.PickaxeEnchantType;
import de.prison.plugin.pickaxe.PickaxeManager;
import de.prison.plugin.world.PrisonWorldGenerator;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ProgressService {

    private final PrisonPlugin plugin;
    private final BlockTypeRegistry blockTypeRegistry;
    private final PlayerDataManager playerDataManager;
    private final PrisonWorldGenerator worldGenerator;
    private final PickaxeManager pickaxeManager;

    public ProgressService(PrisonPlugin plugin) {
        this.plugin = plugin;
        this.blockTypeRegistry = plugin.getBlockTypeRegistry();
        this.playerDataManager = plugin.getPlayerDataManager();
        this.worldGenerator = plugin.getWorldGenerator();
        this.pickaxeManager = plugin.getPickaxeManager();
    }

    public void addProgress(Player player, ItemStack pickaxe, int amountBlocks) {
        PlayerProgress progress = playerDataManager.getProgress(player.getUniqueId());
        PrisonBlockType blockType = blockTypeRegistry.get(progress.getBlockTypeIndex());
        if (blockType == null) return;

        long baseShades = plugin.getConfig().getLong("shades-per-block", 1) * amountBlocks;
        int shadesBoosterLevel = pickaxeManager.getLevel(pickaxe, PickaxeEnchantType.SHADES_BOOSTER);
        double shadesBoosterPercent = plugin.getConfig().getDouble("shades-booster-percent-per-level", 1.0);
        long shadesGained = Math.round(baseShades * (1.0 + (shadesBoosterLevel * shadesBoosterPercent / 100.0)));
        playerDataManager.addShades(player.getUniqueId(), shadesGained);

        progress.setBlocksMinedInStage(progress.getBlocksMinedInStage() + amountBlocks);

        PrisonStage stage = blockType.getStage(progress.getStageNumber());
        if (stage == null) return;

        if (progress.getBlocksMinedInStage() >= stage.getBlocksRequired()) {
            completeStage(player, progress, blockType, stage);
        }

        playerDataManager.save();
    }

    private void completeStage(Player player, PlayerProgress progress, PrisonBlockType blockType, PrisonStage stage) {
        Economy economy = plugin.getEconomy();
        ItemStack pickaxe = player.getInventory().getItemInMainHand();
        int moneyBoosterLevel = pickaxeManager.getLevel(pickaxe, PickaxeEnchantType.MONEY_BOOSTER);
        double moneyBoosterPercent = plugin.getConfig().getDouble("money-booster-percent-per-level", 1.0);
        double moneyGained = stage.getMoneyReward() * (1.0 + (moneyBoosterLevel * moneyBoosterPercent / 100.0));

        if (economy != null) {
            economy.depositPlayer(player, moneyGained);
        }

        player.sendMessage(plugin.msg("stage-complete")
                .replace("%stage%", String.valueOf(stage.getStageNumber()))
                .replace("%money%", formatMoney(moneyGained)));

        progress.setBlocksMinedInStage(0);

        boolean wasLastStage = stage.getStageNumber() >= 10;

        if (!wasLastStage) {
            progress.setStageNumber(stage.getStageNumber() + 1);
            return;
        }

        player.sendMessage(plugin.msg("blocktype-complete").replace("%block%", blockType.getDisplayName()));

        if (blockType.isLast()) {
            progress.incrementLoopCount();
            progress.setStageNumber(1);
            player.sendMessage(plugin.msg("final-block-loop"));
        } else {
            PrisonBlockType nextType = blockTypeRegistry.get(blockType.getIndex() + 1);
            progress.setBlockTypeIndex(nextType.getIndex());
            progress.setStageNumber(1);

            World world = worldGenerator.generateOrLoad(nextType);
            player.teleport(worldGenerator.getSpawnLocation(world));

            player.sendMessage(plugin.msg("blocktype-unlocked").replace("%block%", nextType.getDisplayName()));
        }
    }

    private String formatMoney(double value) {
        if (value == Math.floor(value)) return String.valueOf((long) value);
        return String.format("%.2f", value);
    }
}
