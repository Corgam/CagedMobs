package com.corgam.cagedmobs.configs;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {

    private final ForgeConfigSpec spec;

    private final ForgeConfigSpec.BooleanValue hoppingCagesDisabled;

    public ServerConfig(){
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Server side config for CagedMobs. If changed it will affect the whole server! ");
        builder.push("server");

        builder.comment("Disables Hopping Cages' automatic harvest, making them work the same as the non-hopping variant.");
        this.hoppingCagesDisabled = builder.define("hoppingCagesDisabled", false);

        builder.pop();
        this.spec = builder.build();
    }

    public ForgeConfigSpec getForgeConfigSpec() {
        return spec;
    }

    public boolean hoppingCagesDisabled() {
        return hoppingCagesDisabled.get();
    }
}
