package com.corgam.cagedmobs.setup;

import com.corgam.cagedmobs.blocks.HoppingMobCageBlock;
import com.corgam.cagedmobs.blocks.MobCageBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CagedBlocks {
    // Registries
    public static final DeferredRegister<Block> BLOCKS_REG = DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.MOD_ID);

    // CAGES
    public final static RegistryObject<Block> MOB_CAGE = BLOCKS_REG.register("mobcage", () -> new MobCageBlock(Block.Properties.of(Material.METAL, MaterialColor.METAL).strength(5.0F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));
    public final static RegistryObject<Block> HOPPING_MOB_CAGE = BLOCKS_REG.register("hoppingmobcage", () -> new HoppingMobCageBlock(Block.Properties.of(Material.METAL, MaterialColor.METAL).strength(5.0F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));

}
