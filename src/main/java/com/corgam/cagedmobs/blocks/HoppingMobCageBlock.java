package com.corgam.cagedmobs.blocks;

import com.corgam.cagedmobs.TE.MobCageTE;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class HoppingMobCageBlock extends MobCageBlock{
    public HoppingMobCageBlock(Properties properties) {
        super(properties);
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new MobCageTE(true);
    }
}
