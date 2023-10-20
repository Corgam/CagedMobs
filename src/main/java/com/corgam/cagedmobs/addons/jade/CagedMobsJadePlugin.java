package com.corgam.cagedmobs.addons.jade;

import com.corgam.cagedmobs.blocks.mob_cage.MobCageBlock;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class CagedMobsJadePlugin implements IWailaPlugin {

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(new CagedMobsComponentProvider(), MobCageBlock.class);
    }
}