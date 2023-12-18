package com.corgam.cagedmobs.registers;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.blocks.mob_cage.MobCageBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CagedBlockEntities {
    //Registry
    public static final DeferredRegister<BlockEntityType<?>> CAGED_BLOCK_ENTITIES_REGISTER = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, CagedMobs.MOD_ID);

    // Cage Entity
    public final static Supplier<BlockEntityType<MobCageBlockEntity>> MOB_CAGE_BLOCK_ENTITY = CAGED_BLOCK_ENTITIES_REGISTER.register("mob_cage", () -> BlockEntityType.Builder.of(MobCageBlockEntity::new, CagedBlocks.MOB_CAGE.get(), CagedBlocks.HOPPING_MOB_CAGE.get()).build(null));
}