package com.corgam.cagedmobs.registers;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.blocks.mob_cage.MobCageBlockEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CagedBlockEntities {
    //Registry
    public static final DeferredRegister<TileEntityType<?>> TE_REG = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, CagedMobs.MOD_ID);

    // Cage Entity
    public final static RegistryObject<TileEntityType<MobCageBlockEntity>> MOB_CAGE_BLOCK_ENTITY = TE_REG.register("mob_cage", () -> TileEntityType.Builder.of(MobCageBlockEntity::new, CagedBlocks.MOB_CAGE.get(), CagedBlocks.HOPPING_MOB_CAGE.get()).build(null));
}