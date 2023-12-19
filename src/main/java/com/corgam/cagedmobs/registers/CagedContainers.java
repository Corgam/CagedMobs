package com.corgam.cagedmobs.registers;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.blocks.mob_cage.MobCageMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CagedContainers {

    public static final DeferredRegister<MenuType<?>> CAGED_MENU_TYPES_REGISTER = DeferredRegister.create(BuiltInRegistries.MENU, CagedMobs.MOD_ID);

    // Mob Cages
    public static final Supplier<MenuType<MobCageMenu>> CAGE_CONTAINER = CAGED_MENU_TYPES_REGISTER.register("mob_cage",
            ()-> IMenuTypeExtension.create(((windowId, inv, extraData) -> new MobCageMenu(windowId, inv.player, extraData.readBlockPos()))));
}
