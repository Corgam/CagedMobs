package com.corgam.cagedmobs.registers;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.blocks.*;
import com.corgam.cagedmobs.blocks.mob_cage.HoppingMobCageBlock;
import com.corgam.cagedmobs.blocks.mob_cage.MobCageBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CagedBlocks {
    // Registries
    public static final DeferredRegister<Block> BLOCKS_REG = DeferredRegister.create(ForgeRegistries.BLOCKS, CagedMobs.MOD_ID);

    // CAGES
    public final static RegistryObject<Block> MOB_CAGE = BLOCKS_REG.register("mob_cage", () -> new MobCageBlock(Block.Properties.of(Material.METAL, MaterialColor.METAL).sound(SoundType.METAL).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
    public final static RegistryObject<Block> HOPPING_MOB_CAGE = BLOCKS_REG.register("hopping_mob_cage", () -> new HoppingMobCageBlock(Block.Properties.of(Material.METAL, MaterialColor.METAL).sound(SoundType.METAL).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
    // BLOCKS
    public final static RegistryObject<Block> STAR_INFUSED_NETHERITE_BLOCK = BLOCKS_REG.register("star_infused_netherite_block", () -> new StarInfusedNetheriteBlock(Block.Properties.of(Material.METAL, MaterialColor.COLOR_BLACK).sound(SoundType.NETHERITE_BLOCK).requiresCorrectToolForDrops().strength(50.0F, 1200.0F)));
}
