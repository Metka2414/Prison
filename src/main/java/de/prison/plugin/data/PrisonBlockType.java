package de.prison.plugin.data;

import org.bukkit.Material;

import java.util.List;

public class PrisonBlockType {

    private final int index; // 0-19 (Block 1 bis Block 20)
    private final Material material;
    private final String displayName;
    private final String worldName;
    private final List<PrisonStage> stages;

    public PrisonBlockType(int index, Material material, String displayName, String worldName, List<PrisonStage> stages) {
        this.index = index;
        this.material = material;
        this.displayName = displayName;
        this.worldName = worldName;
        this.stages = stages;
    }

    public int getIndex() {
        return index;
    }

    public int getBlockNumber() {
        return index + 1;
    }

    public Material getMaterial() {
        return material;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getWorldName() {
        return worldName;
    }

    public List<PrisonStage> getStages() {
        return stages;
    }

    public PrisonStage getStage(int stageNumber) {
        if (stageNumber < 1 || stageNumber > stages.size()) return null;
        return stages.get(stageNumber - 1);
    }

    public boolean isLast() {
        return index == 19;
    }
}
