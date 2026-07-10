package de.prison.plugin.world;

import de.prison.plugin.data.PrisonBlockType;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public class MineManager {

    private final PrisonWorldGenerator worldGenerator;
    private final Map<Integer, Integer> remainingBlocks = new HashMap<>();

    public MineManager(PrisonWorldGenerator worldGenerator) {
        this.worldGenerator = worldGenerator;
    }

    public int getTotalBlockCount() {
        return PrisonWorldGenerator.SIZE * PrisonWorldGenerator.SIZE * (PrisonWorldGenerator.DEPTH + 1);
    }

    public void initIfNeeded(PrisonBlockType blockType) {
        remainingBlocks.putIfAbsent(blockType.getIndex(), getTotalBlockCount());
    }

    /**
     * Wird bei jedem abgebauten Block innerhalb einer Mine aufgerufen.
     * Gibt zurück, ob die Mine danach komplett leer war und respawnt wurde.
     */
    public boolean onBlockMined(World world, PrisonBlockType blockType, int amount) {
        initIfNeeded(blockType);
        int remaining = remainingBlocks.get(blockType.getIndex()) - amount;

        if (remaining <= 0) {
            worldGenerator.respawnMine(world, blockType);
            remainingBlocks.put(blockType.getIndex(), getTotalBlockCount());
            return true;
        }

        remainingBlocks.put(blockType.getIndex(), remaining);
        return false;
    }

    public int getRemaining(PrisonBlockType blockType) {
        initIfNeeded(blockType);
        return remainingBlocks.get(blockType.getIndex());
    }
}
