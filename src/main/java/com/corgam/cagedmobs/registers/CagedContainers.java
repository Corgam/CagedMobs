package com.corgam.cagedmobs.registers;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.blocks.mob_cage.MobCageContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CagedContainers {

    public static final DeferredRegister<ContainerType<?>> CAGED_MENU_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, CagedMobs.MOD_ID);

    // Mob Cages
    public static final RegistryObject<ContainerType<MobCageContainer>> CAGE_CONTAINER = CAGED_MENU_TYPES.register("mob_cage",
            ()-> IForgeContainerType.create(((windowId, inv, data) -> new MobCageContainer(windowId, inv.player, data.readBlockPos()))));
}
