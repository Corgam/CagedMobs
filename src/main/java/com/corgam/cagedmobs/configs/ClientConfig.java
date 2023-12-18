package com.corgam.cagedmobs.configs;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {

    private final ModConfigSpec spec;

    private final ModConfigSpec.BooleanValue disableEnvsRender;
    private final ModConfigSpec.BooleanValue disableEntitiesRender;
    private final ModConfigSpec.BooleanValue disableGrowthRender;
    private final ModConfigSpec.BooleanValue disableUpgradesParticles;

    public ClientConfig(){
        final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.comment("Client side config for CagedMobs. Do not change when shipping in ModPacks!");
        builder.push("client");

        builder.comment("Disables environments rendering inside cages to save on performance.");
        this.disableEnvsRender = builder.define("disableEnvsRender", false);

        builder.comment("Disables entities models rendering inside cages to save on performance.");
        this.disableEntitiesRender = builder.define("disableEntitiesRender", false);

        builder.comment("Disables entities growth progress rendering inside cages and keeps the size of the entity inside the cage always the same.");
        this.disableGrowthRender = builder.define("disableGrowthRender", false);

        builder.comment("Disables particles emitted by the cage upgrades");
        this.disableUpgradesParticles = builder.define("disableUpgradesParticles", false);

        builder.pop();
        this.spec = builder.build();
    }

    public ModConfigSpec getForgeConfigSpec() {
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
