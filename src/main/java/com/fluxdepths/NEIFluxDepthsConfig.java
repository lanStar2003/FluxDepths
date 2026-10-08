package com.fluxdepths;

import java.util.regex.Pattern;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import com.fluxdepths.shard.Collectors;

import codechicken.nei.SearchField;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.search.ModNameFilter;

/**
 * NEI plugin (NEI finds it by its name). The collectors are GT machines, items of GregTech's machine block, so NEI's
 * "@mod" search only finds them under GregTech. This replaces the "@" search with one that also finds them under
 * FluxDepths: NEI keeps the last provider registered for a prefix, and plugins load after its own.
 */
public class NEIFluxDepthsConfig implements IConfigureNEI {

    @Override
    public void loadConfig() {
        API.addSearchProvider(
            new SearchField.SearchParserProvider('@', "modName", EnumChatFormatting.LIGHT_PURPLE, Filter::new));
    }

    @Override
    public String getName() {
        return FluxDepths.NAME;
    }

    @Override
    public String getVersion() {
        return Tags.VERSION;
    }

    /** NEI's mod name filter, plus the collectors under this mod's name. */
    private static final class Filter extends ModNameFilter {

        private final Pattern pattern;

        Filter(Pattern pattern) {
            super(pattern);
            this.pattern = pattern;
        }

        @Override
        public boolean matches(ItemStack stack) {
            return super.matches(stack) || Collectors.isCollector(stack) && pattern.matcher(FluxDepths.NAME)
                .find();
        }
    }
}
