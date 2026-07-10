package de.prison.plugin.data;

public class PrisonStage {

    private final int stageNumber; // 1-10
    private final int blocksRequired;
    private final double moneyReward;

    public PrisonStage(int stageNumber, int blocksRequired, double moneyReward) {
        this.stageNumber = stageNumber;
        this.blocksRequired = blocksRequired;
        this.moneyReward = moneyReward;
    }

    public int getStageNumber() {
        return stageNumber;
    }

    public int getBlocksRequired() {
        return blocksRequired;
    }

    public double getMoneyReward() {
        return moneyReward;
    }
}
