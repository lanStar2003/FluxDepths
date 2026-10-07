package com.fluxdepths.shard;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.fluxdepths.FluxDepths;
import com.fluxdepths.item.ItemImprint;

import gregtech.api.enums.GTValues;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMapBuilder;

/**
 * The collectors' recipe map. The machines do not look recipes up in it (they work out each ore themselves); it
 * gives them GT's GUI with an imprint slot, and NEI a page per vein: which ores, how often, and where it generates.
 * Should GT refuse the map, the collectors still work, with GT's plain GUI and no NEI page.
 */
public final class ShardRecipes {

    /** Shown on the NEI tab; set once the machines exist. */
    static ItemStack neiIcon;

    private static RecipeMap<?> map;
    private static boolean tried;

    private ShardRecipes() {}

    public static synchronized RecipeMap<?> map() {
        if (!tried) {
            tried = true;
            try {
                map = RecipeMapBuilder.of("fluxdepths.recipe.shard")
                    .maxIO(4, 4, 1, 0)
                    .minInputs(0, 0)
                    .useSpecialSlot()
                    .slotOverlays(
                        (index, isFluid, isOutput, isSpecial) -> isSpecial ? GTUITextures.OVERLAY_SLOT_DATA_STICK
                            : null)
                    .progressBar(GTUITextures.PROGRESSBAR_ARROW)
                    .progressBarSteam(GTUITextures.PROGRESSBAR_ARROW_STEAM)
                    .neiHandlerInfo(b -> neiIcon == null ? b : b.setDisplayStack(neiIcon))
                    .build();
            } catch (Throwable t) {
                FluxDepths.LOG.error("GT refused the shard collector recipe map; collectors work without NEI pages", t);
            }
        }
        return map;
    }

    /** One NEI page per vein generated in this pack, timed like the steam collector. */
    public static void addNeiPages() {
        RecipeMap<?> m = map();
        if (m == null) return;
        for (Veins.Vein v : Veins.all()
            .values()) {
            if (!v.enabled) continue;
            List<ItemStack> outs = new ArrayList<>();
            int[] chances = new int[v.mix.size()];
            for (int i = 0; i < v.mix.size(); i++) {
                ItemStack ore = v.mix.ores()
                    .get(i)
                    .copy();
                ore.stackSize = 1;
                outs.add(ore);
                chances[i] = (int) Math.max(1, Math.round(v.mix.share(i) * 10000));
            }
            GTValues.RA.stdBuilder()
                .special(ItemImprint.forNei(v.name))
                .itemOutputs(outs.toArray(new ItemStack[0]))
                .outputChances(chances)
                .duration(ShardTier.STEAM.ticks)
                .eut(ShardTier.STEAM.energy)
                .fake()
                .addTo(m);
        }
    }
}
