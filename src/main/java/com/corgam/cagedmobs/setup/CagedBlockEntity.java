package com.corgam.cagedmobs.setup;

import com.corgam.cagedmobs.blockEntities.MobCageBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CagedBlockEntity {
    //Registry
    public static final DeferredRegister<BlockEntityType<?>> TE_REG = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Constants.MOD_ID);

    // CAGES
    public final static RegistryObject<BlockEntityType<MobCageBlockEntity>> MOB_CAGE = TE_REG.register("mobcage", () -> BlockEntityType.Builder.of(MobCageBlockEntity::new, CagedBlocks.MOB_CAGE.get(), CagedBlocks.HOPPING_MOB_CAGE.get()).build(null));
}