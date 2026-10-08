package com.fluxdepths;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fluxdepths.item.ItemImprint;
import com.fluxdepths.item.ItemImprinter;
import com.fluxdepths.shard.Collectors;
import com.fluxdepths.shard.ShardCrafting;
import com.fluxdepths.shard.ShardRecipes;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * FluxDepths: things from the depths of the flux layer, the energy sea under every world that FluxLite's network
 * runs through. Its first module are the shard collectors.
 */
@Mod(
    modid = FluxDepths.MODID,
    name = FluxDepths.NAME,
    version = Tags.VERSION,
    dependencies = "required-after:gregtech;required-after:visualprospecting")
public class FluxDepths {

    public static final String MODID = "fluxdepths";
    public static final String NAME = "FluxDepths";
    public static final Logger LOG = LogManager.getLogger("FluxDepths");

    public static Item imprinter, imprint;

    public static final CreativeTabs TAB = new CreativeTabs(MODID) {

        @Override
        public Item getTabIconItem() {
            return imprinter;
        }
    };

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        Config.load(e.getSuggestedConfigurationFile());
        imprinter = new ItemImprinter();
        imprint = new ItemImprint();
        GameRegistry.registerItem(imprinter, "imprinter");
        GameRegistry.registerItem(imprint, "imprint");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        // registered even when the module is off, so placed collectors keep their blocks
        Collectors.register();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        if (!Config.shardsEnabled) return;
        if (Config.shardRecipes) ShardCrafting.register();
        try {
            ShardRecipes.addNeiPages();
        } catch (Throwable t) {
            LOG.error("Failed to add the shard collector NEI pages", t);
        }
    }
}
