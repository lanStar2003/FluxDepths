package com.fluxdepths;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

/**
 * {@code config/fluxdepths.cfg}. Every module can be switched off; its blocks stay registered so placed machines do
 * not vanish from a world, only the recipes and the work stop.
 */
public final class Config {

    private static final String SHARDS = "shard_collectors";

    public static boolean shardsEnabled = true;
    public static int shardsFirstId = 24520;
    public static boolean crossDimension = false;
    public static boolean shardRecipes = true;

    private Config() {}

    public static void load(File file) {
        Configuration c = new Configuration(file);
        c.setCategoryComment(
            SHARDS,
            "Shard collectors: single-block machines that echo a sampled ore vein, from the steam age to IV.");
        shardsEnabled = c.getBoolean(
            "enabled",
            SHARDS,
            shardsEnabled,
            "Switch the module off: no recipes, no NEI pages, and placed collectors stop working (they stay in the world).");
        shardsFirstId = c.getInt(
            "firstMachineId",
            SHARDS,
            shardsFirstId,
            1,
            32000,
            "First of the 7 GT machine ids the collectors use. Change it only for a new world or when another mod takes these ids.");
        crossDimension = c.getBoolean(
            "crossDimension",
            SHARDS,
            crossDimension,
            "Let imprints work in any world, not only the one they were taken in.");
        shardRecipes = c.getBoolean(
            "enableDefaultRecipes",
            SHARDS,
            shardRecipes,
            "Register the crafting recipes; switch off to write your own with CraftTweaker.");
        if (c.hasChanged()) c.save();
    }
}
