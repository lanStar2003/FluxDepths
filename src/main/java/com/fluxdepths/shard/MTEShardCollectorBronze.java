package com.fluxdepths.shard;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachine;
import gregtech.api.metatileentity.implementations.MTEBasicMachineBronze;
import gregtech.api.recipe.BasicUIProperties;
import gregtech.api.recipe.RecipeMap;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

/** The steam shard collector: bronze, low pressure steam, vents after every ore like GT's steam machines. */
public class MTEShardCollectorBronze extends MTEBasicMachineBronze implements ShardMachine {

    private final ShardState state = new ShardState();

    public MTEShardCollectorBronze(int id) {
        super(id, "fluxdepths.shard.steam", "Steam Shard Collector", "", ShardTier.STEAM.inputSlots(), 4, false);
    }

    private MTEShardCollectorBronze(String name, String[] description, ITexture[][][] textures) {
        super(name, description, textures, ShardTier.STEAM.inputSlots(), 4, false);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity te) {
        return new MTEShardCollectorBronze(mName, mDescriptionArray, mTextures);
    }

    @Override
    public ShardTier tier() {
        return ShardTier.STEAM;
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
        return null;
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
        Collections.addAll(tooltip, ShardText.description(ShardTier.STEAM));
    }

    /** Steam goes in through any side; no tank slot for it. */
    @Override
    protected BasicUIProperties getUIProperties() {
        return super.getUIProperties().toBuilder()
            .maxFluidInputs(0)
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

    @Override
    public ITexture[] getFrontFacingActive(byte color) {
        return new ITexture[] { super.getFrontFacingActive(color)[0], ShardTextures.front(true) };
    }

    @Override
    public ITexture[] getFrontFacingInactive(byte color) {
        return new ITexture[] { super.getFrontFacingInactive(color)[0], ShardTextures.front(false) };
    }

    @Override
    public ITexture[] getTopFacingActive(byte color) {
        return new ITexture[] { super.getTopFacingActive(color)[0], ShardTextures.top(true) };
    }

    @Override
    public ITexture[] getTopFacingInactive(byte color) {
        return new ITexture[] { super.getTopFacingInactive(color)[0], ShardTextures.top(false) };
    }
}
