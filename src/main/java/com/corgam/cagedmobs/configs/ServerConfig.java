package com.corgam.cagedmobs.configs;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {

    private final ForgeConfigSpec spec;

    private final ForgeConfigSpec.BooleanValue onlyTrueDrops;

    public ServerConfig(){
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Server side config for CagedMobs. If changed it will affect the whole server! ");
        builder.push("server");

        builder.comment("Disables entities drops that are not their 'true' drops and cannot be obtained from killing them in vanilla Minecraft (e.g. Dragon Breath from Ender Dragon)");
        this.onlyTrueDrops = builder.define("onlyTrueDrops", false);

        builder.pop();
        this.spec = builder.build();
    }

    public ForgeConfigSpec getForgeConfigSpec() {
        return spec;
    }

    public boolean ifOnlyTrueDrops() {
        return onlyTrueDrops.get();
    }
}
