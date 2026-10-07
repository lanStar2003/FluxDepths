package com.fluxdepths.shard;

/**
 * GT drill heads ({@code toolHeadDrill}) used as the tip that opens the pinhole into the depths. Each one lasts for a
 * fixed number of ores; a collector takes heads of its own material or better.
 */
public enum DrillHead {

    BRONZE("Bronze", 128),
    STEEL("Steel", 256),
    ALUMINIUM("Aluminium", 384),
    STAINLESS_STEEL("StainlessSteel", 512),
    TITANIUM("Titanium", 768),
    TUNGSTEN_STEEL("TungstenSteel", 1024);

    /** GT material name ({@code Materials.get}). */
    public final String material;
    /** Ores one head lasts for. */
    public final int ores;

    DrillHead(String material, int ores) {
        this.material = material;
        this.ores = ores;
    }
}
