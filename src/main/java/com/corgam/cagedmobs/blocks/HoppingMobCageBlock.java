package com.corgam.cagedmobs.blocks;

public class HoppingMobCageBlock extends MobCageBlock {
    public HoppingMobCageBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(HOPPING, Boolean.valueOf(true)));
    }
}
