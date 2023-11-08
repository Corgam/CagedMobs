//package com.corgam.cagedmobs.addons.jade;
//
//import com.corgam.cagedmobs.CagedMobs;
//import com.corgam.cagedmobs.blocks.mob_cage.MobCageBlockEntity;
//import mcp.mobius.waila.api.IComponentProvider;
//import mcp.mobius.waila.api.IServerDataProvider;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.Level;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.List;
//
//public class HideContainerItemsProvider implements IServerDataProvider<MobCageBlockEntity, ItemStack> {
//
//    public static final ResourceLocation UID = new ResourceLocation(CagedMobs.MOD_ID,"hide_container_items");
//
//    @Override
//    public @Nullable List<ViewGroup<ItemStack>> getGroups(ServerPlayer serverPlayer, ServerLevel serverLevel, MobCageBlockEntity mobCageBlockEntity, boolean b) {
//        return List.of();
//    }
//
//    @Override
//    public ResourceLocation getUid() {
//        return UID;
//    }
//
//    @Override
//    public void appendServerData(CompoundTag compoundTag, ServerPlayer serverPlayer, Level level, MobCageBlockEntity mobCageBlockEntity, boolean b) {
//
//    }
//}
