package com.fluxdepths.shard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ShardTierTest {

    @Test
    void noCollectorReachesTheVoidMiner() {
        for (ShardTier t : ShardTier.values())
            assertTrue(t.perSecond() < ShardTier.VOID_MINER_PER_SECOND, t + " " + t.perSecond());
    }

    @Test
    void everyTierIsFasterThanTheOneBefore() {
        ShardTier[] all = ShardTier.values();
        for (int i = 1; i < all.length; i++) {
            assertTrue(all[i].ticks < all[i - 1].ticks, all[i].name());
            assertTrue(all[i].imprints >= all[i - 1].imprints, all[i].name());
            assertTrue(all[i].minHead.ordinal() >= all[i - 1].minHead.ordinal(), all[i].name());
        }
    }

    @Test
    void theAgreedRates() {
        assertEquals(360, Math.round(ShardTier.STEAM.perHour()));
        assertEquals(720, Math.round(ShardTier.HP_STEAM.perHour()));
        assertEquals(1440, Math.round(ShardTier.LV.perHour()));
        assertEquals(2880, Math.round(ShardTier.HV.perHour()));
        assertEquals(5538, Math.round(ShardTier.IV.perHour()));
    }

    @Test
    void electricTiersFitTheirVoltage() {
        long[] v = { 8, 32, 128, 512, 2048, 8192 };
        for (ShardTier t : ShardTier.values()) {
            if (t.steam()) continue;
            assertTrue(t.energy <= v[t.gtTier], t.name());
            assertTrue(t.energy > v[t.gtTier - 1], t.name() + " should need its own tier");
        }
    }

    @Test
    void drillHeadsFromTheTierUp() {
        assertTrue(ShardTier.STEAM.takes(DrillHead.BRONZE));
        assertTrue(ShardTier.STEAM.takes(DrillHead.TUNGSTEN_STEEL));
        assertFalse(ShardTier.MV.takes(DrillHead.STEEL));
        assertTrue(ShardTier.MV.takes(DrillHead.ALUMINIUM));
        assertFalse(ShardTier.IV.takes(null));
        for (int i = 1; i < DrillHead.values().length; i++)
            assertTrue(DrillHead.values()[i].ores > DrillHead.values()[i - 1].ores);
    }

    @Test
    void onlyElectricTiersFromMvNeedFluid() {
        assertEquals(0, ShardTier.STEAM.fluidPerOre);
        assertEquals(0, ShardTier.LV.fluidPerOre);
        assertTrue(ShardTier.MV.fluidPerOre > 0);
        assertTrue(ShardTier.IV.fluidPerOre > 0);
    }
}
