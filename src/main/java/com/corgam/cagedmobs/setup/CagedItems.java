package com.corgam.cagedmobs.setup;

import com.corgam.cagedmobs.items.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SimpleFoiledItem;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CagedItems {
    //Registry
    public static final DeferredRegister<Item> ITEMS_REG = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);

    // CAGES
    public final static RegistryObject<Item> MOB_CAGE = ITEMS_REG.register("mobcage", () -> new BlockItem(CagedBlocks.MOB_CAGE.get(), new Item.Properties().tab(CagedItemGroup.CAGED_MAIN)));
    public final static RegistryObject<Item> HOPPING_MOB_CAGE = ITEMS_REG.register("hoppingmobcage", () -> new BlockItem(CagedBlocks.HOPPING_MOB_CAGE.get(), new Item.Properties().tab(CagedItemGroup.CAGED_MAIN)));

    // SAMPLER
    public final static RegistryObject<Item> DNA_SAMPLER = ITEMS_REG.register("dnasampler", () -> new DnaSamplerItem(new Item.Properties().stacksTo(1).tab(CagedItemGroup.CAGED_MAIN)));
    public final static RegistryObject<Item> DNA_SAMPLER_DIAMOND = ITEMS_REG.register("dnasamplerdiamond", () -> new DnaSamplerDiamondItem(new Item.Properties().stacksTo(1).tab(CagedItemGroup.CAGED_MAIN)));
    public final static RegistryObject<Item> DNA_SAMPLER_NETHERITE = ITEMS_REG.register("dnasamplernetherite", () -> new DnaSamplerNetheriteItem(new Item.Properties().stacksTo(1).tab(CagedItemGroup.CAGED_MAIN)));

    // UPGRADES
    public final static RegistryObject<Item> COOKING_UPGRADE = ITEMS_REG.register("cookingupgrade", () -> new CookingUpgradeItem(new Item.Properties().tab(CagedItemGroup.CAGED_MAIN)));
    public final static RegistryObject<Item> LIGHTNING_UPGRADE = ITEMS_REG.register("lightningupgrade", () -> new LightningUpgradeItem(new Item.Properties().tab(CagedItemGroup.CAGED_MAIN)));
    public final static RegistryObject<Item> ARROW_UPGRADE = ITEMS_REG.register("arrowupgrade", () -> new ArrowUpgradeItem(new Item.Properties().tab(CagedItemGroup.CAGED_MAIN)));

    // MISC
    public final static RegistryObject<Item> DRAGON_SCALE = ITEMS_REG.register("dragon_scale", () -> new Item(new Item.Properties().rarity(Rarity.EPIC).tab(CagedItemGroup.CAGED_MAIN)));
    public final static RegistryObject<Item> SPONGE_FRAGMENT = ITEMS_REG.register("sponge_fragment", () -> new Item(new Item.Properties().tab(CagedItemGroup.CAGED_MAIN)));
    public final static RegistryObject<Item> NETHER_STAR_FRAGMENT = ITEMS_REG.register("nether_star_fragment", () -> new SimpleFoiledItem(new Item.Properties().rarity(Rarity.UNCOMMON).tab(CagedItemGroup.CAGED_MAIN)));
    public final static RegistryObject<Item> HONEY_DROP = ITEMS_REG.register("honey_drop", () -> new Item(new Item.Properties().tab(CagedItemGroup.CAGED_MAIN)));
    public final static RegistryObject<Item> STAR_INFUSED_NETHERITE_INGOT = ITEMS_REG.register("star_infused_netherite_ingot", () -> new SimpleFoiledItem(new Item.Properties().rarity(Rarity.UNCOMMON).tab(CagedItemGroup.CAGED_MAIN)));
    public final static RegistryObject<Item> STAR_INFUSED_NETHERITE_NUGGET = ITEMS_REG.register("star_infused_netherite_nugget", () -> new SimpleFoiledItem(new Item.Properties().rarity(Rarity.UNCOMMON).tab(CagedItemGroup.CAGED_MAIN)));
    public final static RegistryObject<Item> STAR_INFUSED_NETHERITE_BLOCK = ITEMS_REG.register("star_infused_netherite_block", () -> new BlockItem(CagedBlocks.STAR_INFUSED_NETHERITE_BLOCK.get(), new Item.Properties().rarity(Rarity.UNCOMMON).tab(CagedItemGroup.CAGED_MAIN)));
}
