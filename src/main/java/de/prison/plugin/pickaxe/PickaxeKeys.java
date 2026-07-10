package de.prison.plugin.pickaxe;

import de.prison.plugin.PrisonPlugin;
import org.bukkit.NamespacedKey;

public final class PickaxeKeys {

    private PickaxeKeys() {}

    private static NamespacedKey key(String name) {
        return new NamespacedKey(PrisonPlugin.getInstance(), name);
    }

    public static final String PICKAXE_TAG = "prison_pickaxe";

    public static NamespacedKey pickaxeTag() {
        return key(PICKAXE_TAG);
    }

    public static NamespacedKey enchantLevel(PickaxeEnchantType type) {
        return key("enchant_" + type.name().toLowerCase());
    }
}
