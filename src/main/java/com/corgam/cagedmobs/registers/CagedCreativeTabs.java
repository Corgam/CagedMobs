package com.corgam.cagedmobs.registers;

import com.corgam.cagedmobs.setup.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CagedCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CAGED_CREATIVE_TABS_REG = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Constants.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN_TAB = CAGED_CREATIVE_TABS_REG.register("cagemobs_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.cagedmobstab"))
            .icon(() -> new ItemStack(CagedItems.HOPPING_MOB_CAGE.get()))
            .displayItems(((pParameters, pOutput) -> {
                // Cages
                pOutput.accept(CagedItems.MOB_CAGE.get());
                pOutput.accept(CagedItems.HOPPING_MOB_CAGE.get());
                pOutput.accept(CagedItems.TEST_BLOCK.get());
                // Samplers
                pOutput.accept(CagedItems.DNA_SAMPLER.get());
                pOutput.accept(CagedItems.DNA_SAMPLER_DIAMOND.get());
                pOutput.accept(CagedItems.DNA_SAMPLER_NETHERITE.get());
                // Upgrades
                pOutput.accept(CagedItems.SPEED_I_UPGRADE.get());
                pOutput.accept(CagedItems.SPEED_II_UPGRADE.get());
                pOutput.accept(CagedItems.SPEED_III_UPGRADE.get());
                pOutput.accept(CagedItems.FORTUNE_I_UPGRADE.get());
                pOutput.accept(CagedItems.FORTUNE_II_UPGRADE.get());
                pOutput.accept(CagedItems.FORTUNE_III_UPGRADE.get());
                pOutput.accept(CagedItems.ARROW_UPGRADE.get());
                pOutput.accept(CagedItems.COOKING_UPGRADE.get());
                pOutput.accept(CagedItems.LIGHTNING_UPGRADE.get());
                pOutput.accept(CagedItems.EXPERIENCE_UPGRADE.get());
                // Drop Items
                pOutput.accept(CagedItems.DRAGON_SCALE.get());
                pOutput.accept(CagedItems.NETHER_STAR_FRAGMENT.get());
                pOutput.accept(CagedItems.WARDEN_RECEPTOR.get());
                pOutput.accept(CagedItems.SPONGE_FRAGMENT.get());
                pOutput.accept(CagedItems.HONEY_DROP.get());
                pOutput.accept(CagedItems.MILK_DROP.get());
                // Star-infused Netherite
                pOutput.accept(CagedItems.STAR_INFUSED_NETHERITE_NUGGET.get());
                pOutput.accept(CagedItems.STAR_INFUSED_NETHERITE_INGOT.get());
                pOutput.accept(CagedItems.STAR_INFUSED_NETHERITE_BLOCK.get());
            })).build());

}
