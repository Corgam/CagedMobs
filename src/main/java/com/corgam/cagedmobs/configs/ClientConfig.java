package com.corgam.cagedmobs.configs;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

public class ClientConfig {

    private final ForgeConfigSpec spec;

    private final BooleanValue disableEnvsRender;
    private final BooleanValue disableEntitiesRender;
    private final BooleanValue disableGrowthRender;
    private final BooleanValue disableUpgradesParticles;

    public ClientConfig(){
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Client side config for CagedMobs. Do not change when shipping in ModPacks!");
        builder.push("client");

        builder.comment("Disables environments (block models) render to save on performance.");
        this.disableEnvsRender = builder.define("disableEnvsRender", false);

        builder.comment("Disables entities models render to save on performance.");
        this.disableEntitiesRender = builder.define("disableEntitiesRender", false);

        builder.comment("Disables entities growth progress render and keeps the size of the entity inside the cage always the same.");
        this.disableGrowthRender = builder.define("disableGrowthRender", false);

        builder.comment("Disables particles emitted by the cage upgrades");
        this.disableUpgradesParticles = builder.define("disableUpgradesParticles", false);

        builder.pop();
        this.spec = builder.build();
    }

    public ForgeConfigSpec getForgeConfigSpec() {
        return spec;
    }

    public boolean shouldEnvsRender() {
        return !disableEnvsRender.get();
    }

    public boolean shouldEntitiesRender() {
        return !disableEntitiesRender.get();
    }

    public boolean shouldGrowthRender() {
        return !disableGrowthRender.get();
    }

    public boolean shouldUpgradesParticles() {
        return !disableUpgradesParticles.get();
    }
}
