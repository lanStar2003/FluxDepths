package com.fluxdepths.shard;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.fluxdepths.FluxDepths;
import com.fluxdepths.RecipeGuard;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;

/**
 * Crafting table recipes. The imprinter and the steam collector are bronze-age (ender pearls are the link to the
 * flux layer, as in FluxLite); each later collector is built from the one before it, a hull, circuits, a sensor (it
 * listens to the shard) and motors. Lower case letters are GT tools (d screwdriver, h hammer, w wrench).
 */
public final class ShardCrafting {

    private ShardCrafting() {}

    public static void register() {
        try {
            ItemStack pearl = new ItemStack(Items.ender_pearl);
            RecipeGuard.shaped(
                "Imprinter",
                new ItemStack(FluxDepths.imprinter),
                new Object[] { "PGP", "SCS", "dRh", 'P', plate(Materials.Bronze), 'G', "paneGlass", 'S',
                    OrePrefixes.screw.get(Materials.Bronze), 'C', new ItemStack(Items.compass), 'R',
                    OrePrefixes.stick.get(Materials.Bronze) });

            RecipeGuard.shaped(
                "Steam Shard Collector",
                Collectors.get(ShardTier.STEAM),
                new Object[] { "PEP", "GHG", "TDT", 'P', plate(Materials.Bronze), 'E', pearl, 'G',
                    OrePrefixes.gearGt.get(Materials.Bronze), 'H', ItemList.Hull_Bronze.get(1), 'T',
                    OrePrefixes.pipeMedium.get(Materials.Bronze), 'D', head(DrillHead.BRONZE) });
            RecipeGuard.shaped(
                "High Pressure Steam Shard Collector",
                Collectors.get(ShardTier.HP_STEAM),
                new Object[] { "PEP", "GHG", "TXT", 'P', plate(Materials.Steel), 'E', pearl, 'G',
                    OrePrefixes.gearGt.get(Materials.Steel), 'H', ItemList.Hull_HP.get(1), 'T',
                    OrePrefixes.pipeMedium.get(Materials.Steel), 'X', Collectors.get(ShardTier.STEAM) });

            electric(
                ShardTier.LV,
                ShardTier.HP_STEAM,
                Materials.LV,
                ItemList.Sensor_LV,
                ItemList.Electric_Motor_LV,
                ItemList.Hull_LV,
                Materials.Tin);
            electric(
                ShardTier.MV,
                ShardTier.LV,
                Materials.MV,
                ItemList.Sensor_MV,
                ItemList.Electric_Motor_MV,
                ItemList.Hull_MV,
                Materials.AnnealedCopper);
            electric(
                ShardTier.HV,
                ShardTier.MV,
                Materials.HV,
                ItemList.Sensor_HV,
                ItemList.Electric_Motor_HV,
                ItemList.Hull_HV,
                Materials.Gold);
            electric(
                ShardTier.EV,
                ShardTier.HV,
                Materials.EV,
                ItemList.Sensor_EV,
                ItemList.Electric_Motor_EV,
                ItemList.Hull_EV,
                Materials.Aluminium);
            electric(
                ShardTier.IV,
                ShardTier.EV,
                Materials.IV,
                ItemList.Sensor_IV,
                ItemList.Electric_Motor_IV,
                ItemList.Hull_IV,
                Materials.Tungsten);
        } catch (Throwable t) {
            FluxDepths.LOG.error("Failed to register the shard collector recipes", t);
        }
    }

    private static void electric(ShardTier tier, ShardTier from, Materials circuit, ItemList sensor, ItemList motor,
        ItemList hull, Materials cable) {
        RecipeGuard.shaped(
            tier.name() + " Shard Collector",
            Collectors.get(tier),
            new Object[] { "CSC", "MHM", "WXW", 'C', OrePrefixes.circuit.get(circuit), 'S', sensor.get(1), 'M',
                motor.get(1), 'H', hull.get(1), 'W', OrePrefixes.cableGt01.get(cable), 'X', Collectors.get(from) });
    }

    private static Object plate(Materials m) {
        return OrePrefixes.plate.get(m);
    }

    private static Object head(DrillHead h) {
        ItemStack s = DrillHeads.item(h);
        return s != null ? s : OrePrefixes.toolHeadDrill.get(Materials.get(h.material));
    }
}
