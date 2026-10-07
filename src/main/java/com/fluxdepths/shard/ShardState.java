package com.fluxdepths.shard;

import net.minecraft.nbt.NBTTagCompound;

/** What a collector keeps between cycles: how far the current drill head still goes, and whose turn it is. */
public final class ShardState {

    public enum Status {
        IDLE,
        WORKING,
        NO_IMPRINT,
        WRONG_WORLD,
        NO_HEAD,
        NO_FLUID,
        OUTPUT_FULL,
        DISABLED
    }

    /** Ores the drill head in use still lasts for. */
    public int drillLeft;
    /** Index of the imprint whose turn is next. */
    public int next;
    public Status status = Status.IDLE;
    /** Vein of the last ore, for Waila. */
    public String lastVein = "";

    public void save(NBTTagCompound t) {
        t.setInteger("fdDrillLeft", drillLeft);
        t.setInteger("fdNext", next);
    }

    public void load(NBTTagCompound t) {
        drillLeft = Math.max(0, t.getInteger("fdDrillLeft"));
        next = Math.max(0, t.getInteger("fdNext"));
    }
}
