package com.corgam.cagedmobs.configs;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Collections;
import java.util.List;

public class ServerConfig {

    private final ForgeConfigSpec spec;

    private final ForgeConfigSpec.BooleanValue hoppingCagesDisabled;
    private final ForgeConfigSpec.BooleanValue listInWhitelistMode;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> entitiesList;

    public ServerConfig(){
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Server side config for CagedMobs. If changed it will affect the whole server!");
        builder.push("server");

        builder.comment("Disables Hopping Cages' automatic harvest, making them work the same as the non-hopping variant.");
        this.hoppingCagesDisabled = builder.define("hoppingCagesDisabled", false);

        builder.comment("List of all entities blacklisted from use (in a modid:name format). Players will not be able to put entities from this list into cages nor sample them.\n" +
                "To switch this list into a whitelist, set the 'listInWhitelistMode' value to true. \n" +
                "Example use: when 'entitiesList' contains [\"minecraft:pig\", \"minecraft:cow\",\"minecraft:rabbit\"] vanilla pig, cow and rabbit will be blacklisted.");
        this.entitiesList = builder.defineList("entitiesList", Collections.emptyList(), it -> it instanceof String);

        builder.comment("Changes the entities list into a whitelist (only entities inside 'entitiesList' will be available). \n" +
                "Example use: when 'listInWhitelistMode' value is set to true and 'entitiesList' contains [\"minecraft:pig\", \"minecraft:chicken\"] all entities will be blacklisted except vanilla pig and chicken.");
        this.listInWhitelistMode = builder.define("listInWhitelistMode", false);

        builder.pop();
        this.spec = builder.build();
    }

    public ForgeConfigSpec getForgeConfigSpec() {
        return this.spec;
    }

    public boolean ifHoppingCagesDisabled() {
        return this.hoppingCagesDisabled.get();
    }

    public boolean isListInWhitelistMode() {
        return this.listInWhitelistMode.get();
    }

    public List<? extends String> getEntitiesList(){
        return this.entitiesList.get();
    }
}
