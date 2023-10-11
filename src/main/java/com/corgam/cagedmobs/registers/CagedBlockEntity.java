package com.corgam.cagedmobs.registers;

import com.corgam.cagedmobs.block_entities.OldTestEntity;
import com.corgam.cagedmobs.block_entities.MobCageBlockEntity;
import com.corgam.cagedmobs.setup.Constants;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CagedBlockEntity {
    //Registry
    public static final DeferredRegister<BlockEntityType<?>> TE_REG = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Constants.MOD_ID);

    // CAGES
    public final static RegistryObject<BlockEntityType<OldTestEntity>> MOB_CAGE = TE_REG.register("mobcage", () -> BlockEntityType.Builder.of(OldTestEntity::new, CagedBlocks.MOB_CAGE.get(), CagedBlocks.HOPPING_MOB_CAGE.get()).build(null));
    public final static RegistryObject<BlockEntityType<MobCageBlockEntity>> TEST_ENTITY = TE_REG.register("testentity", () -> BlockEntityType.Builder.of(MobCageBlockEntity::new, CagedBlocks.TEST_BLOCK.get(), CagedBlocks.HOPPING_TEST_BLOCK.get()).build(null));

}