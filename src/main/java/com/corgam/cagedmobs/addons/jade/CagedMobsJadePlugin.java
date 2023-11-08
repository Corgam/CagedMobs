package com.corgam.cagedmobs.addons.jade;

import com.corgam.cagedmobs.blocks.mob_cage.MobCageBlock;
import com.corgam.cagedmobs.blocks.mob_cage.MobCageBlockEntity;
import mcp.mobius.waila.api.*;

@WailaPlugin
public class CagedMobsJadePlugin implements IWailaPlugin {

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerComponentProvider(new CagedMobsComponentProvider(), TooltipPosition.BODY, MobCageBlock.class);
    }

//    @Override
//    public void register(IWailaCommonRegistration registration){
//        registration.registerBlockDataProvider(new HideContainerItemsProvider(), MobCageBlockEntity.class);
//    }
}