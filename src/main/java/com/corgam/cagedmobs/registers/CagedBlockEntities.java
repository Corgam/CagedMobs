package com.corgam.cagedmobs.registers;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.blocks.mob_cage.MobCageBlockEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CagedBlockEntities {
    //Registry
    public static final DeferredRegister<TileEntityType<?>> CAGED_BLOCK_ENTITIES_REGISTER = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, CagedMobs.MOD_ID);

    // Cage Entity
    public final static RegistryObject<TileEntityType<MobCageBlockEntity>> MOB_CAGE_BLOCK_ENTITY = CAGED_BLOCK_ENTITIES_REGISTER.register("mob_cage", () -> TileEntityType.Builder.of(MobCageBlockEntity::new, CagedBlocks.MOB_CAGE.get(), CagedBlocks.HOPPING_MOB_CAGE.get()).build(null));
}