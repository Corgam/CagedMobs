package com.corgam.cagedmobs.addons.jade;

import com.corgam.cagedmobs.blocks.MobCageBlock;
import mcp.mobius.waila.api.*;

@WailaPlugin
public class CagedMobsHwylaPlugin implements IWailaPlugin {
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        // As block entity data is synced to client
        // there is no need to register another server data provider
        registration.registerComponentProvider(new CagedMobsComponentProvider(), TooltipPosition.BODY, MobCageBlock.class);
    }
}