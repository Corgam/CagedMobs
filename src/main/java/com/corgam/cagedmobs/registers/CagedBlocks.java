package com.corgam.cagedmobs.registers;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.blocks.*;
import com.corgam.cagedmobs.blocks.mob_cage.HoppingMobCageBlock;
import com.corgam.cagedmobs.blocks.mob_cage.MobCageBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CagedBlocks {
    // Registries
    public static final DeferredRegister<Block> CAGED_BLOCKS_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, CagedMobs.MOD_ID);

    // CAGES
    public final static RegistryObject<Block> MOB_CAGE = CAGED_BLOCKS_REGISTER.register("mob_cage", () -> new MobCageBlock(Block.Properties.of(Material.METAL, MaterialColor.METAL).sound(SoundType.METAL).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
    public final static RegistryObject<Block> HOPPING_MOB_CAGE = CAGED_BLOCKS_REGISTER.register("hopping_mob_cage", () -> new HoppingMobCageBlock(Block.Properties.of(Material.METAL, MaterialColor.METAL).sound(SoundType.METAL).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
    // BLOCKS
    public final static RegistryObject<Block> STAR_INFUSED_NETHERITE_BLOCK = CAGED_BLOCKS_REGISTER.register("star_infused_netherite_block", () -> new StarInfusedNetheriteBlock(Block.Properties.of(Material.METAL, MaterialColor.COLOR_BLACK).sound(SoundType.NETHERITE_BLOCK).requiresCorrectToolForDrops().strength(50.0F, 1200.0F)));
    public final static RegistryObject<Block> CRYSTALLIZED_EXPERIENCE_BLOCK = CAGED_BLOCKS_REGISTER.register("crystallized_experience_block", () -> new CrystallizedExperienceBlock(Block.Properties.of(Material.CLAY, MaterialColor.COLOR_LIGHT_GREEN).sound(SoundType.SLIME_BLOCK).noOcclusion()));
}
