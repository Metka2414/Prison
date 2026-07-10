package de.prison.plugin.gui;

import de.prison.plugin.pickaxe.PickaxeEnchantType;
import de.prison.plugin.pickaxe.PickaxeManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PickaxeEnchantGUI {

    public static final String TITLE = "§8Spitzhacke verzaubern";

    private static final Map<Integer, PickaxeEnchantType> SLOT_MAP = new LinkedHashMap<>();
    static {
        SLOT_MAP.put(10, PickaxeEnchantType.EFFICIENCY);
        SLOT_MAP.put(12, PickaxeEnchantType.HASTE);
        SLOT_MAP.put(14, PickaxeEnchantType.EXPLOSION);
        SLOT_MAP.put(16, PickaxeEnchantType.LAYER);
        SLOT_MAP.put(20, PickaxeEnchantType.MONEY_BOOSTER);
        SLOT_MAP.put(24, PickaxeEnchantType.SHADES_BOOSTER);
    }

    public void open(Player player, ItemStack pickaxe, PickaxeManager pickaxeManager, long shades) {
        Inventory inv = Bukkit.createInventory(null, 36, TITLE);

        for (Map.Entry<Integer, PickaxeEnchantType> entry : SLOT_MAP.entrySet()) {
            int slot = entry.getKey();
            PickaxeEnchantType type = entry.getValue();
            int currentLevel = pickaxeManager.getLevel(pickaxe, type);
            inv.setItem(slot, buildItem(type, currentLevel, shades));
        }

        inv.setItem(31, closeItem());

        player.openInventory(inv);
    }

    private ItemStack buildItem(PickaxeEnchantType type, int currentLevel, long shades) {
        Material material = switch (type) {
            case EFFICIENCY -> Material.DIAMOND_PICKAXE;
            case HASTE -> Material.GOLDEN_PICKAXE;
            case EXPLOSION -> Material.TNT;
            case LAYER -> Material.BEDROCK;
            case MONEY_BOOSTER -> Material.EMERALD;
            case SHADES_BOOSTER -> Material.AMETHYST_SHARD;
        };

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        boolean maxed = currentLevel >= type.getMaxLevel();
        meta.setDisplayName("§e§l" + type.getDisplayName() + " §7(" + currentLevel + "/" + type.getMaxLevel() + ")");

        List<String> lore = new ArrayList<>();
        lore.add("§7Aktuelle Stufe: §b" + currentLevel);
        lore.add("§7Maximale Stufe: §b" + type.getMaxLevel());
        lore.add("");

        if (maxed) {
            lore.add("§6§lBereits maximale Stufe!");
        } else {
            long cost = type.getCostForNextLevel(currentLevel);
            boolean canAfford = shades >= cost;
            lore.add("§7Nächste Stufe kostet: " + (canAfford ? "§a" : "§c") + cost + " Shades");
            lore.add("");
            lore.add(canAfford ? "§a▸ Klicke zum Kaufen" : "§cNicht genug Shades");
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack closeItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§cSchließen");
        item.setItemMeta(meta);
        return item;
    }

    public PickaxeEnchantType getTypeAtSlot(int slot) {
        return SLOT_MAP.get(slot);
    }
}
