package com.fluxdepths.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.fluxdepths.FluxDepths;
import com.fluxdepths.shard.Veins;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * A vein imprint: which shard of the depths a vein was projected from (the vein name VisualProspecting records), and
 * the world it was taken in. Collectors only resonate with imprints of their own world.
 */
public class ItemImprint extends Item {

    private static final String VEIN = "vein", DIM = "dim", DIM_NAME = "dimName", X = "x", Z = "z";
    /** NEI's imprints belong to no particular world. */
    public static final int ANY_DIM = Integer.MIN_VALUE;

    public ItemImprint() {
        setUnlocalizedName("fluxdepths.imprint");
        setTextureName(FluxDepths.MODID + ":imprint");
        setMaxStackSize(1);
        setCreativeTab(FluxDepths.TAB);
    }

    public static ItemStack of(String vein, int dim, String dimName, int x, int z) {
        ItemStack s = new ItemStack(FluxDepths.imprint);
        NBTTagCompound t = new NBTTagCompound();
        t.setString(VEIN, vein);
        t.setInteger(DIM, dim);
        if (dimName != null) t.setString(DIM_NAME, dimName);
        t.setInteger(X, x);
        t.setInteger(Z, z);
        s.setTagCompound(t);
        return s;
    }

    /** An imprint for NEI's recipe pages. */
    public static ItemStack forNei(String vein) {
        return of(vein, ANY_DIM, null, 0, 0);
    }

    public static boolean is(ItemStack s) {
        return s != null && s.getItem() instanceof ItemImprint;
    }

    /** The vein name, or null for a blank imprint. */
    public static String vein(ItemStack s) {
        NBTTagCompound t = is(s) ? s.getTagCompound() : null;
        return t != null && t.hasKey(VEIN) ? t.getString(VEIN) : null;
    }

    public static int dim(ItemStack s) {
        NBTTagCompound t = s.getTagCompound();
        return t != null && t.hasKey(DIM) ? t.getInteger(DIM) : ANY_DIM;
    }

    @Override
    public String getItemStackDisplayName(ItemStack s) {
        Veins.Vein v = Veins.get(vein(s));
        String base = super.getItemStackDisplayName(s);
        return v == null ? base
            : StatCollector.translateToLocalFormatted("item.fluxdepths.imprint.of", v.displayName());
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInformation(ItemStack s, EntityPlayer player, List lines, boolean advanced) {
        Veins.Vein v = Veins.get(vein(s));
        if (v == null) {
            lines.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("fluxdepths.imprint.blank"));
            return;
        }
        for (int i = 0; i < v.mix.size(); i++) {
            ItemStack ore = v.mix.ores()
                .get(i);
            lines.add(
                EnumChatFormatting.GRAY + String.format(" %s  %.1f%%", ore.getDisplayName(), v.mix.share(i) * 100));
        }
        NBTTagCompound t = s.getTagCompound();
        int dim = dim(s);
        if (dim == ANY_DIM) {
            lines.add(
                EnumChatFormatting.DARK_AQUA
                    + StatCollector.translateToLocalFormatted("fluxdepths.imprint.dims", String.join(", ", v.dims)));
        } else {
            String name = t.hasKey(DIM_NAME) ? t.getString(DIM_NAME) : String.valueOf(dim);
            lines.add(
                EnumChatFormatting.DARK_AQUA + StatCollector
                    .translateToLocalFormatted("fluxdepths.imprint.world", name, t.getInteger(X), t.getInteger(Z)));
        }
        lines.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal("fluxdepths.imprint.lore"));
    }
}
