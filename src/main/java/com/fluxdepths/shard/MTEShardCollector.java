package com.fluxdepths.shard;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.ItemList;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachine;
import gregtech.api.recipe.BasicUIProperties;
import gregtech.api.recipe.RecipeMap;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

/** An electric shard collector, LV to IV: a GT single-block machine whose "recipe" is the next ore of its veins. */
public class MTEShardCollector extends MTEBasicMachine implements ShardMachine {

    /** Drilling fluid the tank holds. */
    static final int TANK = 16_000;

    private final ShardTier tier;
    private final ShardState state = new ShardState();

    public MTEShardCollector(int id, ShardTier tier) {
        super(
            id,
            "fluxdepths.shard." + tier.key(),
            "Shard Collector (" + tier.name() + ")",
            tier.gtTier,
            1,
            new String[0],
            tier.inputSlots(),
            4,
            ShardTextures.electricOverlays());
        this.tier = tier;
    }

    private MTEShardCollector(String name, ShardTier tier, String[] description, ITexture[][][] textures) {
        super(name, tier.gtTier, 1, description, textures, tier.inputSlots(), 4);
        this.tier = tier;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity te) {
        return new MTEShardCollector(mName, tier, mDescriptionArray, mTextures);
    }

    @Override
    public ShardTier tier() {
        return tier;
    }

    @Override
    public ShardState state() {
        return state;
    }

    @Override
    public MTEBasicMachine machine() {
        return this;
    }

    @Override
    public boolean fits(ItemStack stack) {
        return canOutput(stack);
    }

    @Override
    public FluidStack tank() {
        return getFillableStack();
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return ShardRecipes.map();
    }

    @Override
    public int checkRecipe() {
        return ShardWork.check(this);
    }

    /** Empty: GT would store the first description it sees in its own lang file, in whatever language. */
    @Override
    public String[] getDescription() {
        return new String[0];
    }

    @Override
    public void addAdditionalTooltipInformation(ItemStack stack, List<String> tooltip) {
        Collections.addAll(tooltip, ShardText.description(tier));
    }

    @Override
    public int getCapacity() {
        return tier.fluidPerOre > 0 ? TANK : 0;
    }

    @Override
    public boolean isFluidInputAllowed(FluidStack f) {
        return tier.fluidPerOre > 0 && f != null && f.getFluid() == ItemList.sDrillingFluid;
    }

    @Override
    protected BasicUIProperties getUIProperties() {
        return super.getUIProperties().toBuilder()
            .maxFluidInputs(tier.fluidPerOre > 0 ? 1 : 0)
            .build();
    }

    @Override
    public void saveNBTData(NBTTagCompound t) {
        super.saveNBTData(t);
        state.save(t);
    }

    @Override
    public void loadNBTData(NBTTagCompound t) {
        super.loadNBTData(t);
        state.load(t);
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        ShardText.wailaData(state, tag);
    }

    @Override
    public void getWailaBody(ItemStack stack, List<String> tip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(stack, tip, accessor, config);
        ShardText.wailaBody(accessor.getNBTData(), tip);
    }
}
