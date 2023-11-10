package com.corgam.cagedmobs.addons.theoneprobe;

import com.corgam.cagedmobs.CagedMobs;
import com.google.common.base.Function;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class CagedMobsTOPSupport implements Function<ITheOneProbe, Void> {
    @Override
    public Void apply(ITheOneProbe probe){
        if(probe != null){
            probe.registerProvider(new IProbeInfoProvider() {
                @Override
                public String getID() {
                    return new ResourceLocation(CagedMobs.MOD_ID,"default").toString();
                }

                @Override
                public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, PlayerEntity playerEntity, World world, BlockState blockState, IProbeHitData iProbeHitData) {
                    if(blockState.getBlock() instanceof ITopInfoProvider){
                        ITopInfoProvider provider = (ITopInfoProvider) blockState.getBlock();
                        provider.addProbeInfo(probeMode,iProbeInfo,playerEntity,world,blockState,iProbeHitData);
                    }
                }
            });
        }
        return null;
    }

}
