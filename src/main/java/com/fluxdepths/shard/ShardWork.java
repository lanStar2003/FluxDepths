package com.fluxdepths.shard;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import com.fluxdepths.Config;
import com.fluxdepths.item.ItemImprint;

import gregtech.api.enums.ItemList;
import gregtech.api.metatileentity.implementations.MTEBasicMachine;

/**
 * One cycle of a shard collector, in the place of GT's recipe lookup: take the next imprint's turn, make sure the
 * drill head and drilling fluid are there, and condense one ore of that vein.
 */
public final class ShardWork {

    /** GT's results of {@code checkRecipe}. */
    static final int NOTHING = 0, BLOCKED = 1, STARTED = 2;

    private ShardWork() {}

    public static int check(ShardMachine m) {
        MTEBasicMachine mte = m.machine();
        ShardTier tier = m.tier();
        ShardState s = m.state();
        if (!Config.shardsEnabled) {
            s.status = ShardState.Status.DISABLED;
            return NOTHING;
        }
        World world = mte.getBaseMetaTileEntity()
            .getWorld();
        int dim = world.provider.dimensionId;

        List<Veins.Vein> veins = new ArrayList<>(tier.imprints);
        boolean foreign = false;
        int first = mte.getInputSlot(), end = first + mte.mInputSlotCount;
        for (int i = -1; i < end - first && veins.size() < tier.imprints; i++) {
            ItemStack st = mte.mInventory[i < 0 ? mte.getSpecialSlotIndex() : first + i];
            Veins.Vein v = Veins.get(ItemImprint.vein(st));
            if (v == null) continue;
            if (!Config.crossDimension && ItemImprint.dim(st) != dim) {
                foreign = true;
                continue;
            }
            veins.add(v);
        }
        if (veins.isEmpty()) {
            s.status = foreign ? ShardState.Status.WRONG_WORLD : ShardState.Status.NO_IMPRINT;
            return NOTHING;
        }

        int headSlot = -1;
        DrillHead head = null;
        if (s.drillLeft <= 0) {
            for (int i = first; i < end && head == null; i++) {
                DrillHead h = DrillHeads.of(mte.mInventory[i]);
                if (tier.takes(h)) {
                    head = h;
                    headSlot = i;
                }
            }
            if (head == null) {
                s.status = ShardState.Status.NO_HEAD;
                return NOTHING;
            }
        }

        FluidStack tank = null;
        if (tier.fluidPerOre > 0) {
            tank = m.tank();
            if (tank == null || tank.getFluid() != ItemList.sDrillingFluid || tank.amount < tier.fluidPerOre) {
                s.status = ShardState.Status.NO_FLUID;
                return NOTHING;
            }
        }

        int turn = Math.floorMod(s.next, veins.size());
        Veins.Vein vein = veins.get(turn);
        ItemStack ore = vein.mix.pick(world.rand.nextDouble());
        if (ore == null) return NOTHING;
        ore = ore.copy();
        ore.stackSize = 1;
        if (!m.fits(ore)) {
            s.status = ShardState.Status.OUTPUT_FULL;
            mte.mOutputBlocked++;
            return BLOCKED;
        }

        s.next = (turn + 1) % veins.size();
        if (head != null) {
            mte.mInventory[headSlot].stackSize--;
            s.drillLeft += head.ores;
        }
        s.drillLeft--;
        if (tank != null) tank.amount -= tier.fluidPerOre;
        mte.mOutputItems[0] = ore;
        mte.mEUt = tier.energy;
        mte.mMaxProgresstime = tier.ticks;
        s.status = ShardState.Status.WORKING;
        s.lastVein = vein.name;
        return STARTED;
    }
}
