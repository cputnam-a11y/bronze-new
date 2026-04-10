package com.khazoda.bronze.block;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class BronzeDoor extends DoorBlock {
  public BronzeDoor(ResourceKey<Block> id) {
    super(BlockSetType.IRON, Properties.of().noOcclusion().requiresCorrectToolForDrops()
        .strength(5.5f)
        .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
        .pushReaction(PushReaction.DESTROY)
        .mapColor(MapColor.GOLD)
        .sound(SoundType.METAL)
        .setId(id));
  }
}
