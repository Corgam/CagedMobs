package com.corgam.cagedmobs.addons.hwyla;

import com.corgam.cagedmobs.blocks.MobCageBlock;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;

@WailaPlugin
public class CagedMobsHwylaPlugin implements IWailaPlugin {
    @Override
    public void register(IRegistrar iRegistrar) {
        iRegistrar.registerComponentProvider(new CagedMobsComponentProvider(), TooltipPosition.BODY, MobCageBlock.class);
    }
}