package com.corgam.cagedmobs.registers;

import com.corgam.cagedmobs.CagedMobs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CagedCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CAGED_CREATIVE_TABS_REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CagedMobs.MOD_ID);

    public static final Supplier<CreativeModeTab> MAIN_TAB = CAGED_CREATIVE_TABS_REGISTER.register("cagedmobs_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.cagedmobs_tab"))
            .icon(() -> new ItemStack(CagedItems.HOPPING_MOB_CAGE.get()))
            .displayItems(((pParameters, pOutput) -> {
                // Cages
                pOutput.accept(CagedItems.MOB_CAGE.get());
                pOutput.accept(CagedItems.HOPPING_MOB_CAGE.get());
                // Samplers
                pOutput.accept(CagedItems.DNA_SAMPLER.get());
                pOutput.accept(CagedItems.DIAMOND_DNA_SAMPLER.get());
                pOutput.accept(CagedItems.NETHERITE_DNA_SAMPLER.get());
                // Upgrades
                pOutput.accept(CagedItems.SPEED_I_UPGRADE.get());
                pOutput.accept(CagedItems.SPEED_II_UPGRADE.get());
                pOutput.accept(CagedItems.SPEED_III_UPGRADE.get());
                pOutput.accept(CagedItems.LOOTING_UPGRADE.get());
                pOutput.accept(CagedItems.ARROW_UPGRADE.get());
                pOutput.accept(CagedItems.COOKING_UPGRADE.get());
                pOutput.accept(CagedItems.LIGHTNING_UPGRADE.get());
                pOutput.accept(CagedItems.EXPERIENCE_UPGRADE.get());
                pOutput.accept(CagedItems.CREATIVE_UPGRADE.get());
                // Drop Items
                pOutput.accept(CagedItems.DRAGON_SCALE.get());
                pOutput.accept(CagedItems.NETHER_STAR_FRAGMENT.get());
                pOutput.accept(CagedItems.WARDEN_RECEPTOR.get());
                pOutput.accept(CagedItems.SPONGE_FRAGMENT.get());
                pOutput.accept(CagedItems.HONEY_DROP.get());
                pOutput.accept(CagedItems.MILK_DROP.get());
                pOutput.accept(CagedItems.CRYSTALLIZED_EXPERIENCE.get());
                pOutput.accept(CagedItems.CRYSTALLIZED_EXPERIENCE_BLOCK.get());
                // Star-infused Netherite
                pOutput.accept(CagedItems.STAR_INFUSED_NETHERITE_NUGGET.get());
                pOutput.accept(CagedItems.STAR_INFUSED_NETHERITE_INGOT.get());
                pOutput.accept(CagedItems.STAR_INFUSED_NETHERITE_BLOCK.get());
                // Empty spawn egg
                pOutput.accept(CagedItems.EMPTY_SPAWN_EGG.get());
            })).build());

}
