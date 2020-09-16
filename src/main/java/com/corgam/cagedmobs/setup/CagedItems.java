package com.corgam.cagedmobs.setup;

import com.corgam.cagedmobs.items.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CagedItems {
    //Registry
    public static final DeferredRegister<Item> ITEMS_REG = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);

    // CAGES
    public final static RegistryObject<Item> MOB_CAGE = ITEMS_REG.register("mobcage", () -> new BlockItem(CagedBlocks.MOB_CAGE.get(), new Item.Properties().group(CagedItemGroup.CAGED_MAIN)));
    public final static RegistryObject<Item> HOPPING_MOB_CAGE = ITEMS_REG.register("hoppingmobcage", () -> new BlockItem(CagedBlocks.HOPPING_MOB_CAGE.get(), new Item.Properties().group(CagedItemGroup.CAGED_MAIN)));

    // PICKER
    public final static RegistryObject<Item> DNA_SAMPLER = ITEMS_REG.register("dnasampler", () -> new DnaSamplerItem(new Item.Properties().maxStackSize(1).group(CagedItemGroup.CAGED_MAIN)));
    public final static RegistryObject<Item> DNA_SAMPLER_DIAMOND = ITEMS_REG.register("dnasamplerdiamond", () -> new DnaSamplerDiamondItem(new Item.Properties().maxStackSize(1).group(CagedItemGroup.CAGED_MAIN)));
    public final static RegistryObject<Item> DNA_SAMPLER_NETHERITE = ITEMS_REG.register("dnasamplernetherite", () -> new DnaSamplerNetheriteItem(new Item.Properties().maxStackSize(1).group(CagedItemGroup.CAGED_MAIN)));

    // UPGRADES
    public final static RegistryObject<Item> COOKING_UPGRADE = ITEMS_REG.register("cookingupgrade", () -> new CookingUpgradeItem(new Item.Properties().group(CagedItemGroup.CAGED_MAIN)));
    public final static RegistryObject<Item> LIGHTNING_UPGRADE = ITEMS_REG.register("lightningupgrade", () -> new LightningUpgradeItem(new Item.Properties().group(CagedItemGroup.CAGED_MAIN)));

}
