package de.prison.plugin.gui;

import de.prison.plugin.PrisonPlugin;
import de.prison.plugin.data.PlayerProgress;
import de.prison.plugin.data.PrisonBlockType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PrisonMainGUI {

    public static final String TITLE = "§8Prison-Menü";

    public static final int SHADES_SLOT = 4;
    public static final int PICKAXE_SLOT = 11;
    public static final int TELEPORT_SLOT = 13;
    public static final int ENCHANT_SLOT = 15;

    private final PrisonPlugin plugin;

    public PrisonMainGUI(PrisonPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE);

        PlayerProgress progress = plugin.getPlayerDataManager().getProgress(player.getUniqueId());
        PrisonBlockType blockType = plugin.getBlockTypeRegistry().get(progress.getBlockTypeIndex());
        long shades = plugin.getPlayerDataManager().getShades(player.getUniqueId());

        inv.setItem(SHADES_SLOT, buildItem(Material.NETHER_STAR, "§d§lDeine Shades",
                List.of("§7Aktuell: §d" + shades + " Shades",
                        "§7Bekommst du beim Abbauen",
                        "§7in den Prison-Minen.")));

        inv.setItem(PICKAXE_SLOT, buildItem(Material.DIAMOND_PICKAXE, "§b§lPrison-Spitzhacke",
                List.of("§7Klicke, um deine Spitzhacke", "§7zu erhalten (falls verloren).")));

        inv.setItem(TELEPORT_SLOT, buildItem(Material.ENDER_PEARL, "§a§lZu deinem Block teleportieren",
                List.of("§7Aktuelle Blockart: §e" + (blockType != null ? blockType.getDisplayName() : "?"),
                        "§7Stufe: §e" + progress.getStageNumber() + "§7/10",
                        "§7Fortschritt: §e" + progress.getBlocksMinedInStage(),
                        "",
                        "§7Klicke zum Teleportieren.")));

        inv.setItem(ENCHANT_SLOT, buildItem(Material.ENCHANTED_BOOK, "§5§lVerzauberungen kaufen",
                List.of("§7Effizienz, Haste, Explosion,", "§7Layer, Geld-Booster, Shades-Booster",
                        "", "§7Klicke zum Öffnen.")));

        player.openInventory(inv);
    }

    private ItemStack buildItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(new ArrayList<>(lore));
        item.setItemMeta(meta);
        return item;
    }
}
