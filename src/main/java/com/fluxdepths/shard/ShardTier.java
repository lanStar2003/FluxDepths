package com.fluxdepths.shard;

/**
 * The seven shard collectors. Each condenses one ore every {@link #ticks} ticks from the veins it is tuned to, paying
 * {@link #energy} per tick (litres of steam for the two steam tiers, EU for the electric ones). The fastest one stays
 * below GTNH's Void Miner I (2 ores a second without noble gases): a pinhole into the depths never matches a tear.
 */
public enum ShardTier {

    // GT tier, ticks, energy, imprints, first drill head, drilling fluid per ore
    STEAM(1, 200, 16, 1, DrillHead.BRONZE, 0),
    HP_STEAM(2, 100, 32, 1, DrillHead.STEEL, 0),
    LV(1, 50, 24, 2, DrillHead.STEEL, 0),
    MV(2, 33, 96, 2, DrillHead.ALUMINIUM, 20),
    HV(3, 25, 384, 3, DrillHead.STAINLESS_STEEL, 20),
    EV(4, 18, 1536, 3, DrillHead.TITANIUM, 20),
    IV(5, 13, 6144, 4, DrillHead.TUNGSTEN_STEEL, 20);

    /** GTNH's Void Miner I: two ores a second, the ceiling of every collector. */
    public static final double VOID_MINER_PER_SECOND = 2;

    /** GT voltage tier (steam: 1 bronze, 2 high pressure). */
    public final int gtTier;
    /** Ticks per ore. */
    public final int ticks;
    /** Steam litres or EU per tick while working. */
    public final int energy;
    /** Imprints it can hold; the ores are shared out between them, not added up. */
    public final int imprints;
    /** The weakest drill head it takes; better ones work too. */
    public final DrillHead minHead;
    /** Litres of drilling fluid per ore, 0 when it needs none. */
    public final int fluidPerOre;

    ShardTier(int gtTier, int ticks, int energy, int imprints, DrillHead minHead, int fluidPerOre) {
        this.gtTier = gtTier;
        this.ticks = ticks;
        this.energy = energy;
        this.imprints = imprints;
        this.minHead = minHead;
        this.fluidPerOre = fluidPerOre;
    }

    public boolean steam() {
        return this == STEAM || this == HP_STEAM;
    }

    public double perSecond() {
        return 20.0 / ticks;
    }

    public double perHour() {
        return perSecond() * 3600;
    }

    /** Input slots: one per imprint beyond the special slot, plus one for drill heads. */
    public int inputSlots() {
        return imprints;
    }

    /** Lower-case key used in names and lang keys, e.g. {@code hp_steam}. */
    public String key() {
        return name().toLowerCase();
    }

    public boolean takes(DrillHead head) {
        return head != null && head.ordinal() >= minHead.ordinal();
    }
}
