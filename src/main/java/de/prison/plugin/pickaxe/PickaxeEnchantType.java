package de.prison.plugin.pickaxe;

public enum PickaxeEnchantType {

    EFFICIENCY("Effizienz", 500, 50),
    HASTE("Haste", 300, 80),
    EXPLOSION("Explosion", 200, 220),
    LAYER("Layer", 200, 260),
    MONEY_BOOSTER("Geld-Booster", 100, 500),
    SHADES_BOOSTER("Shades-Booster", 100, 500);

    private final String displayName;
    private final int maxLevel;
    private final double baseCost;

    PickaxeEnchantType(String displayName, int maxLevel, double baseCost) {
        this.displayName = displayName;
        this.maxLevel = maxLevel;
        this.baseCost = baseCost;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * Kosten in Shades, um von currentLevel auf currentLevel+1 zu kommen.
     * Steigt progressiv an, damit hohe Stufen deutlich teurer sind.
     */
    public long getCostForNextLevel(int currentLevel) {
        int nextLevel = currentLevel + 1;
        return Math.round(baseCost * Math.pow(nextLevel, 1.3));
    }
}
