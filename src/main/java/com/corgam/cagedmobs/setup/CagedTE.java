package com.corgam.cagedmobs.setup;

import com.corgam.cagedmobs.tileEntities.MobCageTE;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CagedTE {
    //Registry
    public static final DeferredRegister<BlockEntityType<?>> TE_REG = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Constants.MOD_ID);

    // CAGES
    public final static RegistryObject<BlockEntityType<MobCageTE>> MOB_CAGE = TE_REG.register("mobcage", () -> BlockEntityType.Builder.of(MobCageTE::new, CagedBlocks.MOB_CAGE.get(), CagedBlocks.HOPPING_MOB_CAGE.get()).build(null));
}
