package com.corgam.cagedmobs.registers;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.blocks.mob_cage.MobCageBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CagedBlockEntities {
    //Registry
    public static final DeferredRegister<BlockEntityType<?>> TE_REG = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, CagedMobs.MOD_ID);

    // Cage Entity
    public final static RegistryObject<BlockEntityType<MobCageBlockEntity>> MOB_CAGE_BLOCK_ENTITY = TE_REG.register("mob_cage", () -> BlockEntityType.Builder.of(MobCageBlockEntity::new, CagedBlocks.MOB_CAGE.get(), CagedBlocks.HOPPING_MOB_CAGE.get()).build(null));
}