package com.corgam.cagedmobs.registers;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.items.*;
import com.corgam.cagedmobs.items.upgrades.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.item.SimpleFoiledItem;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CagedItems {
    //Registry
    public static final DeferredRegister<Item> CAGED_ITEMS_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, CagedMobs.MOD_ID);

    // CAGES
    public final static RegistryObject<Item> MOB_CAGE = CAGED_ITEMS_REGISTER.register("mob_cage", () -> new BlockItem(CagedBlocks.MOB_CAGE.get(), new Item.Properties().tab(CagedCreativeTab.CAGED_MAIN)));
    public final static RegistryObject<Item> HOPPING_MOB_CAGE = CAGED_ITEMS_REGISTER.register("hopping_mob_cage", () -> new BlockItem(CagedBlocks.HOPPING_MOB_CAGE.get(), new Item.Properties().tab(CagedCreativeTab.CAGED_MAIN)));
    // SAMPLER
    public final static RegistryObject<Item> DNA_SAMPLER = CAGED_ITEMS_REGISTER.register("dna_sampler", () -> new DnaSamplerItem(new Item.Properties().tab(CagedCreativeTab.CAGED_MAIN).stacksTo(1)));
    public final static RegistryObject<Item> DIAMOND_DNA_SAMPLER = CAGED_ITEMS_REGISTER.register("diamond_dna_sampler", () -> new DnaSamplerDiamondItem(new Item.Properties().tab(CagedCreativeTab.CAGED_MAIN).stacksTo(1)));
    public final static RegistryObject<Item> NETHERITE_DNA_SAMPLER = CAGED_ITEMS_REGISTER.register("netherite_dna_sampler", () -> new DnaSamplerNetheriteItem(new Item.Properties().tab(CagedCreativeTab.CAGED_MAIN).stacksTo(1)));
    // UPGRADES
    public final static RegistryObject<Item> SPEED_I_UPGRADE = CAGED_ITEMS_REGISTER.register("speed_i_upgrade", () -> new SpeedIUpgradeItem(new Item.Properties().tab(CagedCreativeTab.CAGED_MAIN)));
    public final static RegistryObject<Item> SPEED_II_UPGRADE = CAGED_ITEMS_REGISTER.register("speed_ii_upgrade", () -> new SpeedIIUpgradeItem(new Item.Properties().tab(CagedCreativeTab.CAGED_MAIN)));
    public final static RegistryObject<Item> SPEED_III_UPGRADE = CAGED_ITEMS_REGISTER.register("speed_iii_upgrade", () -> new SpeedIIIUpgradeItem(new Item.Properties().tab(CagedCreativeTab.CAGED_MAIN)));
    public final static RegistryObject<Item> LOOTING_UPGRADE = CAGED_ITEMS_REGISTER.register("looting_upgrade", () -> new LootingUpgradeItem(new Item.Properties().tab(CagedCreativeTab.CAGED_MAIN)));
    public final static RegistryObject<Item> COOKING_UPGRADE = CAGED_ITEMS_REGISTER.register("cooking_upgrade", () -> new CookingUpgradeItem(new Item.Properties().tab(CagedCreativeTab.CAGED_MAIN)));
    public final static RegistryObject<Item> LIGHTNING_UPGRADE = CAGED_ITEMS_REGISTER.register("lightning_upgrade", () -> new LightningUpgradeItem(new Item.Properties().tab(CagedCreativeTab.CAGED_MAIN)));
    public final static RegistryObject<Item> ARROW_UPGRADE = CAGED_ITEMS_REGISTER.register("arrow_upgrade", () -> new ArrowUpgradeItem(new Item.Properties().tab(CagedCreativeTab.CAGED_MAIN)));
    public final static RegistryObject<Item> EXPERIENCE_UPGRADE = CAGED_ITEMS_REGISTER.register("experience_upgrade", () -> new ExperienceUpgradeItem(new Item.Properties().tab(CagedCreativeTab.CAGED_MAIN)));
    public final static RegistryObject<Item> CREATIVE_UPGRADE = CAGED_ITEMS_REGISTER.register("creative_upgrade", () -> new CreativeUpgradeItem(new Item.Properties().tab(CagedCreativeTab.CAGED_MAIN)));


    // MISC
    public final static RegistryObject<Item> DRAGON_SCALE = CAGED_ITEMS_REGISTER.register("dragon_scale", () -> new Item(new Item.Properties().tab(CagedCreativeTab.CAGED_MAIN).rarity(Rarity.EPIC)));
    public final static RegistryObject<Item> NETHER_STAR_FRAGMENT = CAGED_ITEMS_REGISTER.register("nether_star_fragment", () -> new SimpleFoiledItem(new Item.Properties().tab(CagedCreativeTab.CAGED_MAIN).rarity(Rarity.UNCOMMON)));
    public final static RegistryObject<Item> WARDEN_RECEPTOR = CAGED_ITEMS_REGISTER.register("warden_receptor", () -> new Item(new Item.Properties().tab(CagedCreativeTab.CAGED_MAIN).rarity(Rarity.EPIC)));
    public final static RegistryObject<Item> SPONGE_FRAGMENT = CAGED_ITEMS_REGISTER.register("sponge_fragment", () -> new Item(new Item.Properties().tab(CagedCreativeTab.CAGED_MAIN)));
    public final static RegistryObject<Item> HONEY_DROP = CAGED_ITEMS_REGISTER.register("honey_drop", () -> new Item(new Item.Properties().tab(CagedCreativeTab.CAGED_MAIN)));
    public final static RegistryObject<Item> MILK_DROP = CAGED_ITEMS_REGISTER.register("milk_drop", () -> new Item(new Item.Properties().tab(CagedCreativeTab.CAGED_MAIN)));
    public final static RegistryObject<Item> CRYSTALLIZED_EXPERIENCE = CAGED_ITEMS_REGISTER.register("crystallized_experience", () -> new CrystallizedExperienceItem(new Item.Properties().tab(CagedCreativeTab.CAGED_MAIN)));
    public final static RegistryObject<Item> EMPTY_SPAWN_EGG = CAGED_ITEMS_REGISTER.register("empty_spawn_egg", () -> new EmptySpawnEggItem(new Item.Properties().tab(CagedCreativeTab.CAGED_MAIN)));
    public final static RegistryObject<Item> STAR_INFUSED_NETHERITE_INGOT = CAGED_ITEMS_REGISTER.register("star_infused_netherite_ingot", () -> new SimpleFoiledItem(new Item.Properties().tab(CagedCreativeTab.CAGED_MAIN).rarity(Rarity.UNCOMMON)));
    public final static RegistryObject<Item> STAR_INFUSED_NETHERITE_NUGGET = CAGED_ITEMS_REGISTER.register("star_infused_netherite_nugget", () -> new SimpleFoiledItem(new Item.Properties().tab(CagedCreativeTab.CAGED_MAIN).rarity(Rarity.UNCOMMON)));
    
    // BLOCKS
    public final static RegistryObject<Item> STAR_INFUSED_NETHERITE_BLOCK = CAGED_ITEMS_REGISTER.register("star_infused_netherite_block", () -> new BlockItem(CagedBlocks.STAR_INFUSED_NETHERITE_BLOCK.get(), new Item.Properties().tab(CagedCreativeTab.CAGED_MAIN).rarity(Rarity.UNCOMMON)));
    public final static RegistryObject<Item> CRYSTALLIZED_EXPERIENCE_BLOCK = CAGED_ITEMS_REGISTER.register("crystallized_experience_block", () -> new CrystallizedExperienceBlockItem(CagedBlocks.CRYSTALLIZED_EXPERIENCE_BLOCK.get(), new Item.Properties().tab(CagedCreativeTab.CAGED_MAIN)));

}
