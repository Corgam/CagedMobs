package com.corgam.cagedmobs.blocks.mob_cage;

public class HoppingMobCageBlock extends MobCageBlock {
    public HoppingMobCageBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.FALSE).setValue(HOPPING, Boolean.TRUE));
    }
}
