package com.corgam.cagedmobs.registers;

import com.corgam.cagedmobs.blocks.HoppingMobCageBlock;
import com.corgam.cagedmobs.blocks.MobCageBlock;
import com.corgam.cagedmobs.blocks.StarInfusedNetheriteBlock;
import com.corgam.cagedmobs.setup.Constants;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CagedBlocks {
    // Registries
    public static final DeferredRegister<Block> BLOCKS_REG = DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.MOD_ID);

    // CAGES
    public final static RegistryObject<Block> MOB_CAGE = BLOCKS_REG.register("mobcage", () -> new MobCageBlock(Block.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
    public final static RegistryObject<Block> HOPPING_MOB_CAGE = BLOCKS_REG.register("hoppingmobcage", () -> new HoppingMobCageBlock(Block.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
    // BLOCKS
    public final static RegistryObject<Block> STAR_INFUSED_NETHERITE_BLOCK = BLOCKS_REG.register("star_infused_netherite_block", () -> new StarInfusedNetheriteBlock(Block.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.NETHERITE_BLOCK).requiresCorrectToolForDrops().strength(50.0F, 1200.0F)));
}
