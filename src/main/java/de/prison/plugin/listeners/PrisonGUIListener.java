package de.prison.plugin.listeners;

import de.prison.plugin.PrisonPlugin;
import de.prison.plugin.data.PlayerProgress;
import de.prison.plugin.data.PrisonBlockType;
import de.prison.plugin.gui.PickaxeEnchantGUI;
import de.prison.plugin.gui.PrisonMainGUI;
import de.prison.plugin.pickaxe.PickaxeEnchantType;
import de.prison.plugin.pickaxe.PickaxeManager;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class PrisonGUIListener implements Listener {

    private final PrisonPlugin plugin;
    private final PrisonMainGUI mainGUI;
    private final PickaxeEnchantGUI enchantGUI;

    public PrisonGUIListener(PrisonPlugin plugin, PrisonMainGUI mainGUI, PickaxeEnchantGUI enchantGUI) {
        this.plugin = plugin;
        this.mainGUI = mainGUI;
        this.enchantGUI = enchantGUI;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();

        if (title.equals(PrisonMainGUI.TITLE)) {
            handleMainMenuClick(event);
        } else if (title.equals(PickaxeEnchantGUI.TITLE)) {
            handleEnchantMenuClick(event);
        }
    }

    private void handleMainMenuClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;

        int slot = event.getSlot();
        PickaxeManager pickaxeManager = plugin.getPickaxeManager();

        if (slot == PrisonMainGUI.PICKAXE_SLOT) {
            ItemStack pickaxe = pickaxeManager.createPickaxe();
            player.getInventory().addItem(pickaxe);
            player.sendMessage(PrisonPlugin.color("&aDu hast deine Prison-Spitzhacke erhalten."));
            player.closeInventory();
        } else if (slot == PrisonMainGUI.TELEPORT_SLOT) {
            PlayerProgress progress = plugin.getPlayerDataManager().getProgress(player.getUniqueId());
            PrisonBlockType blockType = plugin.getBlockTypeRegistry().get(progress.getBlockTypeIndex());
            if (blockType == null) return;

            World world = plugin.getWorldGenerator().generateOrLoad(blockType);
            player.teleport(plugin.getWorldGenerator().getSpawnLocation(world));
            player.sendMessage(PrisonPlugin.color("&aDu wurdest zu deinem Block teleportiert."));
            player.closeInventory();
        } else if (slot == PrisonMainGUI.ENCHANT_SLOT) {
            ItemStack pickaxe = findPickaxe(player);
            if (pickaxe == null) {
                player.sendMessage(PrisonPlugin.color("&cDu musst zuerst deine Prison-Spitzhacke holen/halten."));
                return;
            }
            long shades = plugin.getPlayerDataManager().getShades(player.getUniqueId());
            enchantGUI.open(player, pickaxe, pickaxeManager, shades);
        }
    }

    private void handleEnchantMenuClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;

        PickaxeEnchantType type = enchantGUI.getTypeAtSlot(event.getSlot());
        if (type == null) return;

        ItemStack pickaxe = findPickaxe(player);
        if (pickaxe == null) {
            player.sendMessage(PrisonPlugin.color("&cDu musst deine Prison-Spitzhacke halten oder im Inventar haben."));
            return;
        }

        PickaxeManager pickaxeManager = plugin.getPickaxeManager();
        int currentLevel = pickaxeManager.getLevel(pickaxe, type);

        if (currentLevel >= type.getMaxLevel()) {
            player.sendMessage(PrisonPlugin.color("&cDiese Verzauberung ist bereits auf maximaler Stufe."));
            return;
        }

        long cost = type.getCostForNextLevel(currentLevel);
        boolean success = plugin.getPlayerDataManager().removeShadesIfEnough(player.getUniqueId(), cost);

        if (!success) {
            player.sendMessage(PrisonPlugin.color("&cDu hast nicht genug Shades! Benötigt: " + cost));
            return;
        }

        pickaxeManager.setLevel(pickaxe, type, currentLevel + 1);
        plugin.getPlayerDataManager().save();

        player.sendMessage(PrisonPlugin.color("&a" + type.getDisplayName() + " auf Stufe " + (currentLevel + 1) + " verbessert!"));

        long shades = plugin.getPlayerDataManager().getShades(player.getUniqueId());
        enchantGUI.open(player, pickaxe, pickaxeManager, shades);
    }

    private ItemStack findPickaxe(Player player) {
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (plugin.getPickaxeManager().isPrisonPickaxe(hand)) return hand;

        for (ItemStack item : player.getInventory().getContents()) {
            if (plugin.getPickaxeManager().isPrisonPickaxe(item)) return item;
        }
        return null;
    }
}
