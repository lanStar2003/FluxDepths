package com.fluxdepths.shard;

import com.fluxdepths.FluxDepths;

import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.ITexture;
import gregtech.api.render.TextureFactory;

/** Overlays drawn on top of GT's machine casings: a pinhole lens on the front, a resonance grille on top. */
public final class ShardTextures {

    public static final IIconContainer FRONT = icon("front"), FRONT_ACTIVE = icon("front_active"),
        FRONT_GLOW = icon("front_active_glow"), TOP = icon("top"), TOP_ACTIVE = icon("top_active"),
        TOP_GLOW = icon("top_active_glow");

    public static final IIconContainer PUMP = icon("pump_front"), PUMP_ACTIVE = icon("pump_front_active"),
        PUMP_GLOW = icon("pump_front_active_glow");

    private ShardTextures() {}

    private static IIconContainer icon(String name) {
        return new Textures.BlockIcons.CustomIcon(FluxDepths.MODID + ":collector/" + name);
    }

    public static ITexture front(boolean active) {
        return active ? TextureFactory.of(TextureFactory.of(FRONT_ACTIVE), glow(FRONT_GLOW)) : TextureFactory.of(FRONT);
    }

    public static ITexture top(boolean active) {
        return active ? TextureFactory.of(TextureFactory.of(TOP_ACTIVE), glow(TOP_GLOW)) : TextureFactory.of(TOP);
    }

    private static ITexture glow(IIconContainer icon) {
        return TextureFactory.builder()
            .addIcon(icon)
            .glow()
            .build();
    }

    /** GT basic machine overlay slots: 2/3 front, 4/5 top (active/inactive); the rest stay GT's defaults. */
    public static ITexture[] electricOverlays() {
        ITexture[] t = new ITexture[14];
        t[2] = front(true);
        t[3] = front(false);
        t[4] = top(true);
        t[5] = top(false);
        return t;
    }

    /** The fluid pumps: their own front, the collectors' grille on top. */
    public static ITexture[] pumpOverlays() {
        ITexture[] t = new ITexture[14];
        t[2] = TextureFactory.of(TextureFactory.of(PUMP_ACTIVE), glow(PUMP_GLOW));
        t[3] = TextureFactory.of(PUMP);
        t[4] = top(true);
        t[5] = top(false);
        return t;
    }
}
