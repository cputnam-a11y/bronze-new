package com.khazoda.bronze.block;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

public class BronzeTrapdoor extends TrapDoorBlock {
  public BronzeTrapdoor(ResourceKey<Block> id) {
    super(BlockSetType.IRON, Properties.of().noOcclusion().requiresCorrectToolForDrops()
        .strength(3.5f)
        .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
        .sound(SoundType.METAL)
        .setId(id));
  }
}
