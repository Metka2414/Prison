package de.prison.plugin.data;

public class PlayerProgress {

    private int blockTypeIndex; // 0-19
    private int stageNumber;    // 1-10
    private int blocksMinedInStage;
    private int loopCount; // wie oft die 10 Stufen bei Block 20 schon durchlaufen wurden

    public PlayerProgress() {
        this.blockTypeIndex = 0;
        this.stageNumber = 1;
        this.blocksMinedInStage = 0;
        this.loopCount = 0;
    }

    public PlayerProgress(int blockTypeIndex, int stageNumber, int blocksMinedInStage, int loopCount) {
        this.blockTypeIndex = blockTypeIndex;
        this.stageNumber = stageNumber;
        this.blocksMinedInStage = blocksMinedInStage;
        this.loopCount = loopCount;
    }

    public int getBlockTypeIndex() {
        return blockTypeIndex;
    }

    public void setBlockTypeIndex(int blockTypeIndex) {
        this.blockTypeIndex = blockTypeIndex;
    }

    public int getStageNumber() {
        return stageNumber;
    }

    public void setStageNumber(int stageNumber) {
        this.stageNumber = stageNumber;
    }

    public int getBlocksMinedInStage() {
        return blocksMinedInStage;
    }

    public void setBlocksMinedInStage(int blocksMinedInStage) {
        this.blocksMinedInStage = blocksMinedInStage;
    }

    public void incrementBlocksMined() {
        this.blocksMinedInStage++;
    }

    public int getLoopCount() {
        return loopCount;
    }

    public void setLoopCount(int loopCount) {
        this.loopCount = loopCount;
    }

    public void incrementLoopCount() {
        this.loopCount++;
    }
}
