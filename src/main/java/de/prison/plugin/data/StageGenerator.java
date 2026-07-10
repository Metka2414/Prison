package de.prison.plugin.data;

import java.util.ArrayList;
import java.util.List;

public class StageGenerator {

    private static final int STAGE_COUNT = 10;

    /**
     * Erstellt 10 Stufen, bei denen Block-Anzahl und Geld-Belohnung geometrisch
     * von Start- zu Endwert ansteigen (z.B. 8 -> 1512 Blöcke, 750 -> 280000 Geld).
     * Die erste und letzte Stufe treffen exakt die übergebenen Start-/Endwerte.
     */
    public static List<PrisonStage> generate(int startBlocks, int endBlocks, double startMoney, double endMoney) {
        List<PrisonStage> stages = new ArrayList<>();

        double blockRatio = Math.pow((double) endBlocks / startBlocks, 1.0 / (STAGE_COUNT - 1));
        double moneyRatio = Math.pow(endMoney / startMoney, 1.0 / (STAGE_COUNT - 1));

        for (int i = 0; i < STAGE_COUNT; i++) {
            int blocks;
            double money;

            if (i == STAGE_COUNT - 1) {
                blocks = endBlocks;
                money = endMoney;
            } else {
                blocks = (int) Math.round(startBlocks * Math.pow(blockRatio, i));
                money = roundNice(startMoney * Math.pow(moneyRatio, i));
            }

            stages.add(new PrisonStage(i + 1, blocks, money));
        }

        return stages;
    }

    private static double roundNice(double value) {
        if (value >= 10000) return Math.round(value / 100.0) * 100.0;
        if (value >= 1000) return Math.round(value / 10.0) * 10.0;
        return Math.round(value);
    }
}
