package com.corgam.cagedmobs.setup;

import com.corgam.cagedmobs.blocks.HoppingMobCageBlock;
import com.corgam.cagedmobs.blocks.MobCageBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CagedBlocks {
    // Registries
    public static final DeferredRegister<Block> BLOCKS_REG = DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.MOD_ID);

    // CAGES
    public final static RegistryObject<Block> MOB_CAGE = BLOCKS_REG.register("mobcage", () -> new MobCageBlock(Block.Properties.create(Material.IRON)));
    public final static RegistryObject<Block> HOPPING_MOB_CAGE = BLOCKS_REG.register("hoppingmobcage", () -> new HoppingMobCageBlock(Block.Properties.create(Material.IRON)));

}
