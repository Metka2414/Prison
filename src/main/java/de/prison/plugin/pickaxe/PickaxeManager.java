package de.prison.plugin.pickaxe;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class PickaxeManager {

    public ItemStack createPickaxe() {
        ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName("§b§lPrison-Spitzhacke");
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);

        meta.getPersistentDataContainer().set(PickaxeKeys.pickaxeTag(), PersistentDataType.BYTE, (byte) 1);
        for (PickaxeEnchantType type : PickaxeEnchantType.values()) {
            meta.getPersistentDataContainer().set(PickaxeKeys.enchantLevel(type), PersistentDataType.INTEGER, 0);
        }

        item.setItemMeta(meta);
        updateAppearance(item);
        return item;
    }

    public boolean isPrisonPickaxe(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(PickaxeKeys.pickaxeTag(), PersistentDataType.BYTE);
    }

    public int getLevel(ItemStack item, PickaxeEnchantType type) {
        if (!isPrisonPickaxe(item)) return 0;
        Integer level = item.getItemMeta().getPersistentDataContainer().get(PickaxeKeys.enchantLevel(type), PersistentDataType.INTEGER);
        return level == null ? 0 : level;
    }

    public void setLevel(ItemStack item, PickaxeEnchantType type, int level) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(PickaxeKeys.enchantLevel(type), PersistentDataType.INTEGER, level);
        item.setItemMeta(meta);
        updateAppearance(item);
    }

    /**
     * Aktualisiert Lore und die sichtbare Effizienz-Verzauberung passend zu den gespeicherten Stufen.
     */
    public void updateAppearance(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        int efficiency = getLevel(item, PickaxeEnchantType.EFFICIENCY);
        if (efficiency > 0) {
            meta.addEnchant(Enchantment.EFFICIENCY, efficiency, true);
        } else {
            meta.removeEnchant(Enchantment.EFFICIENCY);
        }

        List<String> lore = new ArrayList<>();
        lore.add("§7Deine persönliche Prison-Spitzhacke");
        lore.add("§7§m--------------------------");
        for (PickaxeEnchantType type : PickaxeEnchantType.values()) {
            int level = getLevel(item, type);
            lore.add("§e" + type.getDisplayName() + "§7: §b" + level + "§7/§b" + type.getMaxLevel());
        }
        lore.add("§7§m--------------------------");
        lore.add("§d§lUnzerstörbar");
        meta.setLore(lore);

        item.setItemMeta(meta);
    }
}
