package com.khazoda.bronze.block;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

public class TinFramedGlass extends TransparentBlock {
  public TinFramedGlass(ResourceKey<Block> id) {
    super(Properties.of().noOcclusion()
        .instrument(NoteBlockInstrument.HAT)
        .strength(0.7F)
        .sound(SoundType.GLASS)
        .isValidSpawn((state, world, pos, type) -> false)
        .isRedstoneConductor((state, world, pos) -> false)
        .isSuffocating((state, world, pos) -> false)
        .isViewBlocking((state, world, pos) -> false)
        .setId(id));
  }
}
