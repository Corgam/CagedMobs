package com.corgam.cagedmobs.addons.theoneprobe;

import com.corgam.cagedmobs.setup.Constants;
import com.google.common.base.Function;
import mcjty.theoneprobe.api.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CagedMobsTOPSupport implements Function<ITheOneProbe, Void> {


    @Override
    public Void apply(ITheOneProbe probe){
        probe.registerProvider(new IProbeInfoProvider() {
            @Override
            public ResourceLocation getID() {
                return new ResourceLocation(Constants.MOD_ID,"default");
            }

            @Override
            public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player playerEntity, Level world, BlockState blockState, IProbeHitData iProbeHitData) {
                if(blockState.getBlock() instanceof ITopInfoProvider){
                    ITopInfoProvider provider = (ITopInfoProvider) blockState.getBlock();
                    provider.addProbeInfo(probeMode,iProbeInfo,playerEntity,world,blockState,iProbeHitData);
                }
            }
        });
        return null;
    }

}
