package de.prison.plugin.data;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class BlockTypeRegistry {

    private static final int BASE_START_BLOCKS = 8;
    private static final int BASE_END_BLOCKS = 1512;
    private static final double BASE_START_MONEY = 750;
    private static final double BASE_END_MONEY = 280_000;

    private static final double BLOCKS_MULTIPLIER_PER_TYPE = 1.25;
    private static final double MONEY_MULTIPLIER_PER_TYPE = 1.55;

    private static final Material[] MATERIALS = {
            Material.COBBLESTONE,
            Material.STONE,
            Material.COAL_ORE,
            Material.COPPER_ORE,
            Material.IRON_ORE,
            Material.LAPIS_ORE,
            Material.REDSTONE_ORE,
            Material.GOLD_ORE,
            Material.EMERALD_ORE,
            Material.DIAMOND_ORE,
            Material.NETHERRACK,
            Material.NETHER_GOLD_ORE,
            Material.NETHER_QUARTZ_ORE,
            Material.ANCIENT_DEBRIS,
            Material.DEEPSLATE_COAL_ORE,
            Material.DEEPSLATE_IRON_ORE,
            Material.DEEPSLATE_GOLD_ORE,
            Material.DEEPSLATE_DIAMOND_ORE,
            Material.DEEPSLATE_EMERALD_ORE,
            Material.OBSIDIAN
    };

    private static final String[] DISPLAY_NAMES = {
            "Cobblestone", "Stein", "Kohle-Erz", "Kupfer-Erz", "Eisen-Erz",
            "Lapis-Erz", "Redstone-Erz", "Gold-Erz", "Smaragd-Erz", "Diamant-Erz",
            "Netherrack", "Nether-Gold-Erz", "Nether-Quarz-Erz", "Antiker Schrott", "Tiefenschiefer-Kohle-Erz",
            "Tiefenschiefer-Eisen-Erz", "Tiefenschiefer-Gold-Erz", "Tiefenschiefer-Diamant-Erz", "Tiefenschiefer-Smaragd-Erz", "Obsidian"
    };

    private final List<PrisonBlockType> blockTypes = new ArrayList<>();

    public BlockTypeRegistry() {
        generate();
    }

    private void generate() {
        for (int i = 0; i < MATERIALS.length; i++) {
            double blockScale = Math.pow(BLOCKS_MULTIPLIER_PER_TYPE, i);
            double moneyScale = Math.pow(MONEY_MULTIPLIER_PER_TYPE, i);

            int startBlocks = (int) Math.round(BASE_START_BLOCKS * blockScale);
            int endBlocks = (int) Math.round(BASE_END_BLOCKS * blockScale);
            double startMoney = BASE_START_MONEY * moneyScale;
            double endMoney = BASE_END_MONEY * moneyScale;

            List<PrisonStage> stages = StageGenerator.generate(startBlocks, endBlocks, startMoney, endMoney);

            String worldName = "prison_block_" + (i + 1);
            blockTypes.add(new PrisonBlockType(i, MATERIALS[i], DISPLAY_NAMES[i], worldName, stages));
        }
    }

    public List<PrisonBlockType> getAll() {
        return blockTypes;
    }

    public PrisonBlockType get(int index) {
        if (index < 0 || index >= blockTypes.size()) return null;
        return blockTypes.get(index);
    }

    public PrisonBlockType getFirst() {
        return blockTypes.get(0);
    }

    public PrisonBlockType getLast() {
        return blockTypes.get(blockTypes.size() - 1);
    }

    public int size() {
        return blockTypes.size();
    }
}
