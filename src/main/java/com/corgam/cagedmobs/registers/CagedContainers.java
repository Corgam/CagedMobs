package com.corgam.cagedmobs.registers;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.blocks.mob_cage.MobCageContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CagedContainers {

    public static final DeferredRegister<MenuType<?>> CAGED_MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, CagedMobs.MOD_ID);

    // Mob Cages
    public static final RegistryObject<MenuType<MobCageContainer>> CAGE_CONTAINER = CAGED_MENU_TYPES.register("mob_cage",
            ()-> IForgeMenuType.create(((windowId, inv, data) -> new MobCageContainer(windowId, inv.player, data.readBlockPos()))));
}
