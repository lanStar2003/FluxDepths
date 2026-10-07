package com.fluxdepths.shard;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.item.ItemStack;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

/** Which GT item each {@link DrillHead} is. */
public final class DrillHeads {

    private static Map<DrillHead, ItemStack> items;

    private DrillHeads() {}

    private static synchronized Map<DrillHead, ItemStack> items() {
        if (items == null) {
            items = new EnumMap<>(DrillHead.class);
            for (DrillHead h : DrillHead.values()) {
                ItemStack s = GTOreDictUnificator.get(OrePrefixes.toolHeadDrill, Materials.get(h.material), 1);
                if (s != null) items.put(h, s);
            }
        }
        return items;
    }

    /** The GT drill head item, or null when GT does not make one of that material. */
    public static ItemStack item(DrillHead h) {
        ItemStack s = items().get(h);
        return s == null ? null : s.copy();
    }

    /** Which head the stack is, or null. */
    public static DrillHead of(ItemStack stack) {
        if (stack == null) return null;
        for (Map.Entry<DrillHead, ItemStack> e : items().entrySet())
            if (GTUtility.areStacksEqual(stack, e.getValue(), true)) return e.getKey();
        return null;
    }
}
