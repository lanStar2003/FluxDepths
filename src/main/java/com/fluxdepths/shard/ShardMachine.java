package com.fluxdepths.shard;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.metatileentity.implementations.MTEBasicMachine;

/** The parts of a GT basic machine the shard logic needs; the steam and electric collectors share it this way. */
public interface ShardMachine {

    ShardTier tier();

    ShardState state();

    MTEBasicMachine machine();

    /** GT's {@code canOutput}: the stack fits into the output slots. */
    boolean fits(ItemStack stack);

    /** The input tank (drilling fluid), or null. */
    FluidStack tank();
}
