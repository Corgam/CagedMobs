package com.corgam.cagedmobs.configs;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Collections;
import java.util.List;

public class ServerConfig {

    private final ForgeConfigSpec spec;

    private final ForgeConfigSpec.BooleanValue hoppingCagesDisabled;

    private final ForgeConfigSpec.BooleanValue entitiesListInWhitelistMode;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> entitiesList;

    private final ForgeConfigSpec.BooleanValue itemsListInWhitelistMode;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> itemsList;

    private final ForgeConfigSpec.BooleanValue singleUseSamplers;

    private final ForgeConfigSpec.DoubleValue cagesSpeed;

    public ServerConfig(){
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Server side config for CagedMobs. If changed it will affect the whole server!");
        builder.push("server");

        // Disable hopping cages
        builder.comment("Disables Hopping Cages' automatic harvest, making them work the same as the non-hopping variant.");
        this.hoppingCagesDisabled = builder.define("hoppingCagesDisabled", false);

        // Single use samplers
        builder.comment("Makes all samplers (all tiers) only single use. After a mob is sampled and put into the cage the sampler will break.");
        this.singleUseSamplers = builder.define("singleUseSamplers", false);

        // Cage speed
        builder.comment("Sets the speed of all cages. The bigger the value the faster the cages will work (by default: 1.00).");
        this.cagesSpeed = builder.defineInRange("cagesSpeed",1.00,0.01,100.00);

        // Entities list
        builder.comment("List of all entities blacklisted from use (in a modid:name format). Players will not be able to put entities from this list into cages nor sample them.\n" +
                "To switch this list into a whitelist, set the 'entitiesListInWhitelistMode' value to true. \n" +
                "Example use: when 'entitiesList' contains [\"minecraft:pig\", \"minecraft:cow\",\"minecraft:rabbit\"] vanilla pig, cow and rabbit will be blacklisted.");
        this.entitiesList = builder.defineList("entitiesList", Collections.emptyList(), it -> it instanceof String);

        builder.comment("Changes the entities list into a whitelist (only entities inside 'entitiesList' will be available). \n" +
                "Example use: when 'entitiesListInWhitelistMode' value is set to true and 'entitiesList' contains [\"minecraft:pig\", \"minecraft:chicken\"] all entities will be blacklisted except vanilla pig and chicken.");
        this.entitiesListInWhitelistMode = builder.define("entitiesListInWhitelistMode", false);

        // Items list
        builder.comment("List of all items blacklisted from use (in a modid:name format).\n" +
                "Items in this list will not be able to be produced from cages, even if they are present in one or more recipes.\n" +
                "To switch this list into a whitelist, set the 'itemsListInWhitelistMode' value to true. \n" +
                "Example use: when 'itemsList' contains [\"minecraft:iron_ingot\", \"minecraft:gold_ingot\", \"minecraft:wither_rose\"] iron ingot, gold ingot and wither rose will not drop from any cage recipe.");
        this.itemsList = builder.defineList("itemsList", Collections.emptyList(), it -> it instanceof String);

        builder.comment("Changes the items list into a whitelist (only items inside 'itemsList' can drop from caged mobs). \n" +
                "Example use: when 'itemsListInWhitelistMode' value is set to true and 'itemsList' contains [\"minecraft:iron_ingot\"] only iron ingots could be obtained from caged entities.");
        this.itemsListInWhitelistMode = builder.define("itemsListInWhitelistMode", false);

        builder.pop();
        this.spec = builder.build();
    }

    public ForgeConfigSpec getForgeConfigSpec() {
        return this.spec;
    }

    public boolean ifHoppingCagesDisabled() {
        return this.hoppingCagesDisabled.get();
    }

    public boolean isEntitiesListInWhitelistMode() {
        return this.entitiesListInWhitelistMode.get();
    }

    public List<? extends String> getEntitiesList(){
        return this.entitiesList.get();
    }

    public boolean isItemsListInWhitelistMode() {
        return this.itemsListInWhitelistMode.get();
    }

    public List<? extends String> getItemsList(){
        return this.itemsList.get();
    }

    public boolean areSamplersSingleUse(){return this.singleUseSamplers.get();}

    public Double getSpeedOfCages(){return this.cagesSpeed.get();}
}
