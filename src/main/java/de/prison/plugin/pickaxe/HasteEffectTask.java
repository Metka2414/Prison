package de.prison.plugin.pickaxe;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HasteEffectTask implements Runnable {

    private final PickaxeManager pickaxeManager;

    public HasteEffectTask(PickaxeManager pickaxeManager) {
        this.pickaxeManager = pickaxeManager;
    }

    public static void start(JavaPlugin plugin, PickaxeManager pickaxeManager) {
        Bukkit.getScheduler().runTaskTimer(plugin, new HasteEffectTask(pickaxeManager), 20L, 60L);
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ItemStack hand = player.getInventory().getItemInMainHand();

            if (!pickaxeManager.isPrisonPickaxe(hand)) {
                continue;
            }

            int hasteLevel = pickaxeManager.getLevel(hand, PickaxeEnchantType.HASTE);
            if (hasteLevel <= 0) continue;

            // Level (max 300) auf einen sinnvollen Amplifier (max 25) skalieren
            int amplifier = Math.min(hasteLevel / 12, 25);
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 100, amplifier, false, false, false));
        }
    }
}
