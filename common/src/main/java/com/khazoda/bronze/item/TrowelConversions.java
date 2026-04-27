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
import net.minecraft.server.packs.PackResources;
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
import java.util.ArrayList;
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

  private static void apply(HolderLookup.Provider registries, ResourceManager manager, List<PreparedConversionFile> files) {
    HolderLookup.RegistryLookup<Block> blocks = registries.lookupOrThrow(Registries.BLOCK);
    Map<TagKey<Block>, List<Holder<Block>>> blockTags = TagLoader.loadTagsForRegistry(
        manager,
        Registries.BLOCK,
        (id, required) -> blocks.get(ResourceKey.create(Registries.BLOCK, id))
    );
    Map<Block, TrowelConversion> loaded = new LinkedHashMap<>();

    for (PreparedConversionFile file : files) {
      loadFile(blocks, blockTags, file, loaded);
    }

    conversions = Map.copyOf(loaded);
    Constants.LOG.info("Loaded {} trowel conversions", conversions.size());
  }

  private static void loadFile(
      HolderLookup.RegistryLookup<Block> blocks,
      Map<TagKey<Block>, List<Holder<Block>>> blockTags,
      PreparedConversionFile file,
      Map<Block, TrowelConversion> loaded
  ) {
    try {
      Identifier fileId = file.id();
      JsonObject json = file.json();
      Block to = resolveToBlock(blocks, fileId, json);
      @Nullable ResourceKey<LootTable> loot = resolveLoot(fileId, json);
      JsonElement from = GsonHelper.getNonNull(json, "from");

      if (from.isJsonArray()) {
        if (from.getAsJsonArray().isEmpty()) {
          Constants.LOG.warn("Skipping trowel conversion '{}': 'from' must not be an empty array", fileId);
          return;
        }

        for (JsonElement element : from.getAsJsonArray()) {
          resolveFrom(blocks, blockTags, file, element, to, loot, loaded);
        }
      } else {
        resolveFrom(blocks, blockTags, file, from, to, loot, loaded);
      }
    } catch (JsonParseException exception) {
      Constants.LOG.warn("Skipping trowel conversion '{}' from pack '{}': {}", file.id(), file.packId(), exception.getMessage());
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
      PreparedConversionFile file,
      JsonElement element,
      Block to,
      @Nullable ResourceKey<LootTable> loot,
      Map<Block, TrowelConversion> loaded
  ) {
    String raw = GsonHelper.convertToString(element, "from");
    if (raw.startsWith("#")) {
      resolveFromTag(blockTags, file, raw, to, loot, loaded);
    } else {
      Identifier id = parseIdentifier(raw, file.id(), "from");
      Optional<Holder.Reference<Block>> holder = blocks.get(ResourceKey.create(Registries.BLOCK, id));
      if (holder.isEmpty()) {
        Constants.LOG.warn("Skipping unknown trowel conversion source '{}' in '{}' from pack '{}'", id, file.id(), file.packId());
        return;
      }

      addConversion(file, holder.get().value(), to, loot, loaded);
    }
  }

  private static void resolveFromTag(
      Map<TagKey<Block>, List<Holder<Block>>> blockTags,
      PreparedConversionFile file,
      String raw,
      Block to,
      @Nullable ResourceKey<LootTable> loot,
      Map<Block, TrowelConversion> loaded
  ) {
    Identifier id = parseIdentifier(raw.substring(1), file.id(), "from");
    TagKey<Block> tag = TagKey.create(Registries.BLOCK, id);
    List<Holder<Block>> holders = blockTags.get(tag);
    if (holders == null) {
      Constants.LOG.warn("Skipping unknown trowel conversion block tag '#{}' in '{}' from pack '{}'", id, file.id(), file.packId());
      return;
    }

    if (holders.isEmpty()) {
      Constants.LOG.warn("Skipping empty trowel conversion block tag '#{}' in '{}' from pack '{}'", id, file.id(), file.packId());
      return;
    }

    for (Holder<Block> holder : holders) {
      addConversion(file, holder.value(), to, loot, loaded);
    }
  }

  private static void addConversion(
      PreparedConversionFile file,
      Block from,
      Block to,
      @Nullable ResourceKey<LootTable> loot,
      Map<Block, TrowelConversion> loaded
  ) {
    TrowelConversion conversion = new TrowelConversion(from, to, loot);
    TrowelConversion existing = loaded.put(from, conversion);
    if (existing != null) {
      Constants.LOG.warn(
          "Overriding trowel conversion for '{}' with '{}' from pack '{}'; previously mapped to '{}'",
          BuiltInRegistries.BLOCK.getKey(from),
          file.id(),
          file.packId(),
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

  private record PreparedConversionFile(String packId, Identifier id, JsonObject json) {
  }

  public static class ReloadListener extends SimplePreparableReloadListener<List<PreparedConversionFile>> {
    private final HolderLookup.Provider registries;

    public ReloadListener(HolderLookup.Provider registries) {
      this.registries = registries;
    }

    @Override
    protected List<PreparedConversionFile> prepare(ResourceManager manager, ProfilerFiller profiler) {
      List<String> packOrder = manager.listPacks().map(PackResources::packId).toList();
      Map<String, List<PreparedConversionFile>> filesByPack = new LinkedHashMap<>();
      for (String packId : packOrder) {
        filesByPack.put(packId, new ArrayList<>());
      }

      for (Map.Entry<Identifier, List<Resource>> entry : CONVERTER.listMatchingResourceStacks(manager).entrySet()) {
        Identifier location = entry.getKey();
        Identifier id = CONVERTER.fileToId(location);

        for (Resource resource : entry.getValue()) {
          try (Reader reader = resource.openAsReader()) {
            JsonElement json = StrictJsonParser.parse(reader);
            if (!json.isJsonObject()) {
              throw new JsonSyntaxException("Expected trowel conversion to be an object");
            }

            filesByPack.computeIfAbsent(resource.sourcePackId(), ignored -> new ArrayList<>())
                .add(new PreparedConversionFile(resource.sourcePackId(), id, json.getAsJsonObject()));
          } catch (IOException | JsonParseException exception) {
            Constants.LOG.warn("Couldn't parse trowel conversion '{}' from '{}' in pack '{}'", id, location, resource.sourcePackId(), exception);
          }
        }
      }

      List<PreparedConversionFile> orderedFiles = new ArrayList<>();
      for (List<PreparedConversionFile> files : filesByPack.values()) {
        orderedFiles.addAll(files);
      }
      return orderedFiles;
    }

    @Override
    protected void apply(List<PreparedConversionFile> preparations, ResourceManager manager, ProfilerFiller profiler) {
      TrowelConversions.apply(registries, manager, preparations);
    }
  }
}
