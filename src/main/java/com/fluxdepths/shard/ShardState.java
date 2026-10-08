package com.fluxdepths.shard;

import net.minecraft.nbt.NBTTagCompound;

/** What a collector keeps between cycles: whose turn it is; and for Waila, how long its drill head lasts. */
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

    /** Average ores the drill head in use lasts for (0: none yet), for Waila; not saved. */
    public int headUses;
    /** Index of the imprint whose turn is next. */
    public int next;
    public Status status = Status.IDLE;
    /** Vein of the last ore, for Waila. */
    public String lastVein = "";

    public void save(NBTTagCompound t) {
        t.setInteger("fdNext", next);
    }

    public void load(NBTTagCompound t) {
        next = Math.max(0, t.getInteger("fdNext"));
    }
}
