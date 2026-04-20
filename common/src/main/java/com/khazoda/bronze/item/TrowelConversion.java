package com.khazoda.bronze.item;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jspecify.annotations.Nullable;

public record TrowelConversion(Block from, Block to, @Nullable ResourceKey<LootTable> loot) {}
