package com.fluxdepths.shard;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

/** Tooltip and Waila lines of the collectors. */
public final class ShardText {

    private ShardText() {}

    private static String t(String key, Object... args) {
        return StatCollector.translateToLocalFormatted("fluxdepths.shard." + key, args);
    }

    public static String[] description(ShardTier tier) {
        List<String> l = new ArrayList<>();
        l.add(machineType("fluxdepths.shard.type"));
        l.add(EnumChatFormatting.DARK_AQUA + t("lore"));
        l.add(t("speed", fmt(tier.ticks / 20.0), Math.round(tier.perHour())));
        l.add(tier.steam() ? t("steam", tier.energy) : t("eu", tier.energy));
        l.add(t("imprints", tier.imprints));
        l.add(
            t(
                "head",
                StatCollector.translateToLocal(
                    "fluxdepths.head." + tier.minHead.name()
                        .toLowerCase())));
        l.add(t("wear", DrillHead.percent(tier.minHead.ores), tier.minHead.ores));
        if (tier.fluidPerOre > 0) l.add(t("fluid", tier.fluidPerOre));
        l.add(t("world"));
        l.add(
            EnumChatFormatting.DARK_GRAY
                + t("cap", Math.round(tier.perSecond() / ShardTier.VOID_MINER_PER_SECOND * 100)));
        return l.toArray(new String[0]);
    }

    /** GT's "Machine Type: X" line, as its own machines have it ({@code GT5U.MBTT.MachineType} is GT's lang key). */
    public static String machineType(String typeKey) {
        return StatCollector.translateToLocal("GT5U.MBTT.MachineType") + ": "
            + EnumChatFormatting.YELLOW
            + StatCollector.translateToLocal(typeKey)
            + EnumChatFormatting.RESET;
    }

    private static String fmt(double seconds) {
        return seconds == Math.rint(seconds) ? String.valueOf((long) seconds) : String.format("%.2f", seconds);
    }

    public static void wailaData(ShardState s, NBTTagCompound tag) {
        tag.setInteger("fdStatus", s.status.ordinal());
        tag.setInteger("fdHeadUses", s.headUses);
        tag.setString("fdVein", s.lastVein);
    }

    public static void wailaBody(NBTTagCompound tag, List<String> tip) {
        if (!tag.hasKey("fdStatus")) return;
        ShardState.Status[] all = ShardState.Status.values();
        ShardState.Status st = all[Math.max(0, Math.min(all.length - 1, tag.getInteger("fdStatus")))];
        EnumChatFormatting color = st == ShardState.Status.WORKING ? EnumChatFormatting.AQUA
            : st == ShardState.Status.IDLE ? EnumChatFormatting.GRAY : EnumChatFormatting.GOLD;
        tip.add(
            color + StatCollector.translateToLocal(
                "fluxdepths.status." + st.name()
                    .toLowerCase()));
        Veins.Vein v = Veins.get(tag.getString("fdVein"));
        if (v != null && st == ShardState.Status.WORKING) tip.add(t("echoing", v.displayName()));
        int uses = tag.getInteger("fdHeadUses");
        if (uses > 0) tip.add(t("drill_wear", DrillHead.percent(uses), uses));
    }
}
