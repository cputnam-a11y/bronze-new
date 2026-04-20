package com.khazoda.bronze.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.khazoda.bronze.Constants;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.tags.TagLoader;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class TrowelConversions {
  public static final Identifier RELOAD_LISTENER_ID = Constants.ID("trowel_conversions");

  private static final FileToIdConverter CONVERTER = FileToIdConverter.json("trowel_conversions");
  private static Map<Block, TrowelConversion> conversions = Map.of();

  private TrowelConversions() {}

  public static @Nullable TrowelConversion get(BlockState state) {
    return conversions.get(state.getBlock());
  }

  private static void apply(HolderLookup.Provider registries, ResourceManager manager, Map<Identifier, JsonObject> files) {
    HolderLookup.RegistryLookup<Block> blocks = registries.lookupOrThrow(Registries.BLOCK);
    Map<TagKey<Block>, List<Holder<Block>>> blockTags = TagLoader.loadTagsForRegistry(
        manager,
        Registries.BLOCK,
        (id, required) -> blocks.get(ResourceKey.create(Registries.BLOCK, id))
    );
    Map<Block, TrowelConversion> loaded = new LinkedHashMap<>();

    files.entrySet().stream()
        .sorted(Comparator.comparing(entry -> entry.getKey().toString()))
        .forEach(entry -> loadFile(blocks, blockTags, entry.getKey(), entry.getValue(), loaded));

    conversions = Map.copyOf(loaded);
    Constants.LOG.info("Loaded {} trowel conversions", conversions.size());
  }

  private static void loadFile(
      HolderLookup.RegistryLookup<Block> blocks,
      Map<TagKey<Block>, List<Holder<Block>>> blockTags,
      Identifier fileId,
      JsonObject json,
      Map<Block, TrowelConversion> loaded
  ) {
    try {
      Block to = resolveToBlock(blocks, fileId, json);
      @Nullable ResourceKey<LootTable> loot = resolveLoot(fileId, json);
      JsonElement from = GsonHelper.getNonNull(json, "from");

      if (from.isJsonArray()) {
        if (from.getAsJsonArray().isEmpty()) {
          Constants.LOG.warn("Skipping trowel conversion '{}': 'from' must not be an empty array", fileId);
          return;
        }

        for (JsonElement element : from.getAsJsonArray()) {
          resolveFrom(blocks, blockTags, fileId, element, to, loot, loaded);
        }
      } else {
        resolveFrom(blocks, blockTags, fileId, from, to, loot, loaded);
      }
    } catch (JsonParseException exception) {
      Constants.LOG.warn("Skipping trowel conversion '{}': {}", fileId, exception.getMessage());
    }
  }

  private static Block resolveToBlock(HolderLookup.RegistryLookup<Block> blocks, Identifier fileId, JsonObject json) {
    String raw = GsonHelper.getAsString(json, "to");
    if (raw.startsWith("#")) {
      throw new JsonSyntaxException("'to' must be a block id, not a tag: " + raw);
    }

    Identifier id = parseIdentifier(raw, fileId, "to");
    return blocks.get(ResourceKey.create(Registries.BLOCK, id))
        .map(Holder::value)
        .orElseThrow(() -> new JsonSyntaxException("Unknown block in 'to': " + id));
  }

  private static @Nullable ResourceKey<LootTable> resolveLoot(Identifier fileId, JsonObject json) {
    if (!json.has("loot")) return null;

    String raw = GsonHelper.getAsString(json, "loot");
    Identifier id = parseIdentifier(raw, fileId, "loot");
    return ResourceKey.create(Registries.LOOT_TABLE, id);
  }

  private static void resolveFrom(
      HolderLookup.RegistryLookup<Block> blocks,
      Map<TagKey<Block>, List<Holder<Block>>> blockTags,
      Identifier fileId,
      JsonElement element,
      Block to,
      @Nullable ResourceKey<LootTable> loot,
      Map<Block, TrowelConversion> loaded
  ) {
    String raw = GsonHelper.convertToString(element, "from");
    if (raw.startsWith("#")) {
      resolveFromTag(blockTags, fileId, raw, to, loot, loaded);
    } else {
      Identifier id = parseIdentifier(raw, fileId, "from");
      Optional<Holder.Reference<Block>> holder = blocks.get(ResourceKey.create(Registries.BLOCK, id));
      if (holder.isEmpty()) {
        Constants.LOG.warn("Skipping unknown trowel conversion source '{}' in '{}'", id, fileId);
        return;
      }

      addConversion(fileId, holder.get().value(), to, loot, loaded);
    }
  }

  private static void resolveFromTag(
      Map<TagKey<Block>, List<Holder<Block>>> blockTags,
      Identifier fileId,
      String raw,
      Block to,
      @Nullable ResourceKey<LootTable> loot,
      Map<Block, TrowelConversion> loaded
  ) {
    Identifier id = parseIdentifier(raw.substring(1), fileId, "from");
    TagKey<Block> tag = TagKey.create(Registries.BLOCK, id);
    List<Holder<Block>> holders = blockTags.get(tag);
    if (holders == null) {
      Constants.LOG.warn("Skipping unknown trowel conversion block tag '#{}' in '{}'", id, fileId);
      return;
    }

    if (holders.isEmpty()) {
      Constants.LOG.warn("Skipping empty trowel conversion block tag '#{}' in '{}'", id, fileId);
      return;
    }

    for (Holder<Block> holder : holders) {
      addConversion(fileId, holder.value(), to, loot, loaded);
    }
  }

  private static void addConversion(
      Identifier fileId,
      Block from,
      Block to,
      @Nullable ResourceKey<LootTable> loot,
      Map<Block, TrowelConversion> loaded
  ) {
    TrowelConversion conversion = new TrowelConversion(from, to, loot);
    TrowelConversion existing = loaded.putIfAbsent(from, conversion);
    if (existing != null) {
      Constants.LOG.warn(
          "Skipping duplicate trowel conversion for '{}' in '{}'; '{}' is already mapped to '{}'",
          BuiltInRegistries.BLOCK.getKey(from),
          fileId,
          BuiltInRegistries.BLOCK.getKey(existing.from()),
          BuiltInRegistries.BLOCK.getKey(existing.to())
      );
    }
  }

  private static Identifier parseIdentifier(String raw, Identifier fileId, String field) {
    Identifier id = Identifier.tryParse(raw);
    if (id == null) {
      throw new JsonSyntaxException("Invalid identifier in '" + field + "' for '" + fileId + "': " + raw);
    }
    return id;
  }

  public static class ReloadListener extends SimplePreparableReloadListener<Map<Identifier, JsonObject>> {
    private final HolderLookup.Provider registries;

    public ReloadListener(HolderLookup.Provider registries) {
      this.registries = registries;
    }

    @Override
    protected Map<Identifier, JsonObject> prepare(ResourceManager manager, ProfilerFiller profiler) {
      Map<Identifier, JsonObject> result = new LinkedHashMap<>();
      for (Map.Entry<Identifier, Resource> entry : CONVERTER.listMatchingResources(manager).entrySet()) {
        Identifier location = entry.getKey();
        Identifier id = CONVERTER.fileToId(location);

        try (Reader reader = entry.getValue().openAsReader()) {
          JsonElement json = StrictJsonParser.parse(reader);
          if (!json.isJsonObject()) {
            throw new JsonSyntaxException("Expected trowel conversion to be an object");
          }
          result.put(id, json.getAsJsonObject());
        } catch (IOException | JsonParseException exception) {
          Constants.LOG.warn("Couldn't parse trowel conversion '{}' from '{}'", id, location, exception);
        }
      }
      return result;
    }

    @Override
    protected void apply(Map<Identifier, JsonObject> preparations, ResourceManager manager, ProfilerFiller profiler) {
      TrowelConversions.apply(registries, manager, preparations);
    }
  }
}
