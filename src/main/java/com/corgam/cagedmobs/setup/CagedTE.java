package com.corgam.cagedmobs.setup;

import com.corgam.cagedmobs.TileEntities.MobCageTE;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CagedTE {
    //Registry
    public static final DeferredRegister<TileEntityType<?>> TE_REG = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Constants.MOD_ID);

    // CAGES
    public final static RegistryObject<TileEntityType<MobCageTE>> MOB_CAGE = TE_REG.register("mobcage", () -> TileEntityType.Builder.create(MobCageTE::new, CagedBlocks.MOB_CAGE.get(), CagedBlocks.HOPPING_MOB_CAGE.get()).build(null));
}
