package com.corgam.cagedmobs.setup;

import com.corgam.cagedmobs.blocks.HoppingMobCageBlock;
import com.corgam.cagedmobs.blocks.MobCageBlock;
import com.corgam.cagedmobs.blocks.StarInfusedNetheriteBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CagedBlocks {
    // Registries
    public static final DeferredRegister<Block> BLOCKS_REG = DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.MOD_ID);

    // CAGES
    public final static RegistryObject<Block> MOB_CAGE = BLOCKS_REG.register("mobcage", () -> new MobCageBlock(Block.Properties.of(Material.METAL, MaterialColor.METAL).strength(5.0F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));
    public final static RegistryObject<Block> HOPPING_MOB_CAGE = BLOCKS_REG.register("hoppingmobcage", () -> new HoppingMobCageBlock(Block.Properties.of(Material.METAL, MaterialColor.METAL).strength(5.0F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));
    // BLOCKS
    public final static RegistryObject<Block> STAR_INFUSED_NETHERITE_BLOCK = BLOCKS_REG.register("star_infused_netherite_block", () -> new StarInfusedNetheriteBlock(AbstractBlock.Properties.of(Material.METAL, MaterialColor.COLOR_BLACK).requiresCorrectToolForDrops().strength(50.0F, 1200.0F).sound(SoundType.NETHERITE_BLOCK)));
}
