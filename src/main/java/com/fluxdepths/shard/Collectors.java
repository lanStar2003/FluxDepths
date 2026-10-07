package com.fluxdepths.shard;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.item.ItemStack;

import com.fluxdepths.Config;

import gregtech.api.GregTechAPI;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;

/** The seven collectors as GT machines, on consecutive ids from {@link Config#shardsFirstId}. */
public final class Collectors {

    private static final Map<ShardTier, ItemStack> STACKS = new EnumMap<>(ShardTier.class);

    private Collectors() {}

    public static void register() {
        for (ShardTier tier : ShardTier.values()) {
            int id = Config.shardsFirstId + tier.ordinal();
            IMetaTileEntity taken = GregTechAPI.METATILEENTITIES[id];
            if (taken != null) throw new IllegalStateException(
                "FluxDepths: GT machine id " + id
                    + " is already used by "
                    + taken.getClass()
                        .getName()
                    + ". Set shard_collectors.firstMachineId in config/fluxdepths.cfg to the start of 7 free ids.");
            IMetaTileEntity mte = switch (tier) {
                case STEAM -> new MTEShardCollectorBronze(id);
                case HP_STEAM -> new MTEShardCollectorSteel(id);
                default -> new MTEShardCollector(id, tier);
            };
            STACKS.put(tier, mte.getStackForm(1));
        }
        ShardRecipes.neiIcon = get(ShardTier.STEAM);
        ShardRecipes.map();
    }

    public static ItemStack get(ShardTier tier) {
        ItemStack s = STACKS.get(tier);
        return s == null ? null : s.copy();
    }
}
