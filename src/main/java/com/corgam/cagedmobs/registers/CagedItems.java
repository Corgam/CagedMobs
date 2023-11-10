package com.corgam.cagedmobs.registers;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.items.*;
import com.corgam.cagedmobs.items.upgrades.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SimpleFoiledItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CagedItems {
    //Registry
    public static final DeferredRegister<Item> ITEMS_REG = DeferredRegister.create(ForgeRegistries.ITEMS, CagedMobs.MOD_ID);

    // CAGES
    public final static RegistryObject<Item> MOB_CAGE = ITEMS_REG.register("mob_cage", () -> new BlockItem(CagedBlocks.MOB_CAGE.get(), new Item.Properties()));
    public final static RegistryObject<Item> HOPPING_MOB_CAGE = ITEMS_REG.register("hopping_mob_cage", () -> new BlockItem(CagedBlocks.HOPPING_MOB_CAGE.get(), new Item.Properties()));
    // SAMPLER
    public final static RegistryObject<Item> DNA_SAMPLER = ITEMS_REG.register("dna_sampler", () -> new DnaSamplerItem(new Item.Properties().stacksTo(1)));
    public final static RegistryObject<Item> DIAMOND_DNA_SAMPLER = ITEMS_REG.register("diamond_dna_sampler", () -> new DnaSamplerDiamondItem(new Item.Properties().stacksTo(1)));
    public final static RegistryObject<Item> NETHERITE_DNA_SAMPLER = ITEMS_REG.register("netherite_dna_sampler", () -> new DnaSamplerNetheriteItem(new Item.Properties().stacksTo(1)));
    // UPGRADES
    public final static RegistryObject<Item> SPEED_I_UPGRADE = ITEMS_REG.register("speed_i_upgrade", () -> new SpeedIUpgradeItem(new Item.Properties()));
    public final static RegistryObject<Item> SPEED_II_UPGRADE = ITEMS_REG.register("speed_ii_upgrade", () -> new SpeedIIUpgradeItem(new Item.Properties()));
    public final static RegistryObject<Item> SPEED_III_UPGRADE = ITEMS_REG.register("speed_iii_upgrade", () -> new SpeedIIIUpgradeItem(new Item.Properties()));
    public final static RegistryObject<Item> LOOTING_UPGRADE = ITEMS_REG.register("looting_upgrade", () -> new LootingUpgradeItem(new Item.Properties()));
    public final static RegistryObject<Item> COOKING_UPGRADE = ITEMS_REG.register("cooking_upgrade", () -> new CookingUpgradeItem(new Item.Properties()));
    public final static RegistryObject<Item> LIGHTNING_UPGRADE = ITEMS_REG.register("lightning_upgrade", () -> new LightningUpgradeItem(new Item.Properties()));
    public final static RegistryObject<Item> ARROW_UPGRADE = ITEMS_REG.register("arrow_upgrade", () -> new ArrowUpgradeItem(new Item.Properties()));
    public final static RegistryObject<Item> EXPERIENCE_UPGRADE = ITEMS_REG.register("experience_upgrade", () -> new ExperienceUpgradeItem(new Item.Properties()));
    public final static RegistryObject<Item> CREATIVE_UPGRADE = ITEMS_REG.register("creative_upgrade", () -> new CreativeUpgradeItem(new Item.Properties()));


    // MISC
    public final static RegistryObject<Item> DRAGON_SCALE = ITEMS_REG.register("dragon_scale", () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));
    public final static RegistryObject<Item> NETHER_STAR_FRAGMENT = ITEMS_REG.register("nether_star_fragment", () -> new SimpleFoiledItem(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public final static RegistryObject<Item> WARDEN_RECEPTOR = ITEMS_REG.register("warden_receptor", () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));
    public final static RegistryObject<Item> SPONGE_FRAGMENT = ITEMS_REG.register("sponge_fragment", () -> new Item(new Item.Properties()));
    public final static RegistryObject<Item> HONEY_DROP = ITEMS_REG.register("honey_drop", () -> new Item(new Item.Properties()));
    public final static RegistryObject<Item> MILK_DROP = ITEMS_REG.register("milk_drop", () -> new Item(new Item.Properties()));
    public final static RegistryObject<Item> EXPERIENCE_ORB = ITEMS_REG.register("experience_orb", () -> new ExperienceOrb(new Item.Properties()));
    public final static RegistryObject<Item> EMPTY_SPAWN_EGG = ITEMS_REG.register("empty_spawn_egg", () -> new EmptySpawnEggItem(new Item.Properties()));
    public final static RegistryObject<Item> STAR_INFUSED_NETHERITE_INGOT = ITEMS_REG.register("star_infused_netherite_ingot", () -> new SimpleFoiledItem(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public final static RegistryObject<Item> STAR_INFUSED_NETHERITE_NUGGET = ITEMS_REG.register("star_infused_netherite_nugget", () -> new SimpleFoiledItem(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public final static RegistryObject<Item> STAR_INFUSED_NETHERITE_BLOCK = ITEMS_REG.register("star_infused_netherite_block", () -> new BlockItem(CagedBlocks.STAR_INFUSED_NETHERITE_BLOCK.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));
}
