package de.prison.plugin.listeners;

import de.prison.plugin.PrisonPlugin;
import de.prison.plugin.data.BlockTypeRegistry;
import de.prison.plugin.data.PlayerDataManager;
import de.prison.plugin.data.PrisonBlockType;
import de.prison.plugin.pickaxe.PickaxeEnchantType;
import de.prison.plugin.pickaxe.PickaxeManager;
import de.prison.plugin.progress.ProgressService;
import de.prison.plugin.world.MineManager;
import de.prison.plugin.world.PrisonWorldGenerator;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class PrisonMineListener implements Listener {

    private final PrisonPlugin plugin;
    private final BlockTypeRegistry blockTypeRegistry;
    private final PlayerDataManager playerDataManager;
    private final PickaxeManager pickaxeManager;
    private final MineManager mineManager;
    private final ProgressService progressService;

    private final Map<String, Integer> worldToBlockType = new HashMap<>();

    public PrisonMineListener(PrisonPlugin plugin, ProgressService progressService) {
        this.plugin = plugin;
        this.blockTypeRegistry = plugin.getBlockTypeRegistry();
        this.playerDataManager = plugin.getPlayerDataManager();
        this.pickaxeManager = plugin.getPickaxeManager();
        this.mineManager = plugin.getMineManager();
        this.progressService = progressService;

        for (PrisonBlockType type : blockTypeRegistry.getAll()) {
            worldToBlockType.put(type.getWorldName(), type.getIndex());
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        String worldName = event.getBlock().getWorld().getName();
        Integer blockTypeIndex = worldToBlockType.get(worldName);
        if (blockTypeIndex == null) return;

        PrisonBlockType blockType = blockTypeRegistry.get(blockTypeIndex);
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (block.getType() != blockType.getMaterial()) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);
        event.setDropItems(false);

        ItemStack pickaxe = player.getInventory().getItemInMainHand();
        boolean isPrisonPickaxe = pickaxeManager.isPrisonPickaxe(pickaxe);

        int realBroken = 1;
        block.setType(Material.AIR, false);

        int layerLevel = isPrisonPickaxe ? pickaxeManager.getLevel(pickaxe, PickaxeEnchantType.LAYER) : 0;
        if (layerLevel > 0) {
            realBroken += breakLayer(block, blockType, layerLevel);
        }

        int explosionLevel = isPrisonPickaxe ? pickaxeManager.getLevel(pickaxe, PickaxeEnchantType.EXPLOSION) : 0;
        int progressCount = realBroken + explosionLevel;

        mineManager.onBlockMined(block.getWorld(), blockType, realBroken);
        progressService.addProgress(player, pickaxe, progressCount);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        String worldName = event.getBlock().getWorld().getName();
        if (worldToBlockType.containsKey(worldName)) {
            event.setCancelled(true);
        }
    }

    private int breakLayer(Block center, PrisonBlockType blockType, int level) {
        int half = PrisonWorldGenerator.SIZE / 2;
        int maxLevel = PickaxeEnchantType.LAYER.getMaxLevel();
        int radius = (int) Math.round(half * ((double) level / maxLevel));
        if (radius <= 0) return 0;

        World world = center.getWorld();
        int y = center.getY();
        int count = 0;

        for (int x = -half; x <= half; x++) {
            for (int z = -half; z <= half; z++) {
                if (Math.abs(x - center.getX()) > radius || Math.abs(z - center.getZ()) > radius) continue;

                Block b = world.getBlockAt(x, y, z);
                if (b.getType() == blockType.getMaterial()) {
                    b.setType(Material.AIR, false);
                    count++;
                }
            }
        }

        return count;
    }
}
