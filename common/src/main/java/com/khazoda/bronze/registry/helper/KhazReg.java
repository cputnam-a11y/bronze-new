package com.khazoda.bronze.registry.helper;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * KhazReg is an opinionated multiloader registration helper for Khazoda's mods that fits a specific registration pattern.
 * It's not recommended to use this class yourself. Its structure may change over time and there may be breaking changes.
 */
public final class KhazReg {
  private final String modId;
  private final Map<ResourceKey<? extends Registry<?>>, Registrar<?>> registrars = new LinkedHashMap<>();
  private boolean frozen;

  public KhazReg(String modId) {
    this.modId = Objects.requireNonNull(modId, "modId");
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private static <T> Registry<T> builtinRegistry(ResourceKey<? extends Registry<T>> registryKey) {
    Registry root = BuiltInRegistries.REGISTRY;
    Holder.Reference<?> registryHolder = (Holder.Reference<?>) root.get(registryKey).orElse(null);
    if (registryHolder == null) {
      throw new IllegalArgumentException("Unsupported registry " + registryKey + ". KhazReg only supports builtin/static registries.");
    }
    return (Registry<T>) registryHolder.value();
  }

  private Identifier ID(String path) {
    return Identifier.fromNamespaceAndPath(modId, path);
  }

  /**
   * ==========[ Common Registration Helpers ]==========
   * Registration methods to call from MainRegistry
   */

  /* Example: reg.register(Registries.ARMOR_MATERIAL, "bronze", () -> new ArmorMaterial(...)) */
  public <T, T2 extends T> Entry<T2> register(ResourceKey<? extends Registry<T>> registryKey, String name, Supplier<T2> supplier) {
    return registrar(registryKey).register(name, supplier);
  }

  /* Example: reg.register(Registries.ARMOR_MATERIAL, "bronze", key -> new ArmorMaterial(...)) */
  public <T, T2 extends T> Entry<T2> register(ResourceKey<? extends Registry<T>> registryKey, String name, Function<ResourceKey<T2>, T2> factory) {
    return registrar(registryKey).register(name, factory);
  }

  /* Example: reg.item("tin_ingot") */
  public Entry<Item> item(String name) {
    ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, ID(name));
    return register(Registries.ITEM, name, () -> new Item(new Item.Properties().setId(key)));
  }

  /* Example: reg.item("heavy_tin_ingot", (key, props) -> new Item(props.stacksTo(16))) */
  public <T extends Item> Entry<T> item(String name, ItemFactory<T> factory) {
    ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, ID(name));
    return register(Registries.ITEM, name, () -> factory.create(key, new Item.Properties().setId(key)));
  }

  /* Example: reg.block("tin_block") */
  public Entry<Block> block(String name) {
    ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, ID(name));
    return register(Registries.BLOCK, name, () -> new Block(BlockBehaviour.Properties.of().setId(key)));
  }

  /* Example: reg.block("strong_tin_block", (key, props) -> new Block(props.strength(5.0F))) */
  public <T extends Block> Entry<T> block(String name, BlockFactory<T> factory) {
    ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, ID(name));
    return register(Registries.BLOCK, name, () -> factory.create(key, BlockBehaviour.Properties.of().setId(key)));
  }

  /* Example: reg.copyBlock("cut_tin", TIN_BLOCK) */
  public Entry<Block> copyBlock(String name, Supplier<? extends Block> source) {
    ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, ID(name));
    return register(Registries.BLOCK, name, () -> new Block(BlockBehaviour.Properties.ofFullCopy(source.get()).setId(key)));
  }

  /* Example: reg.copyBlock("tin_stairs", TIN_BLOCK, (key, props) -> new StairBlock(TIN_BLOCK.get().defaultBlockState(), props)) */
  public <T extends Block> Entry<T> copyBlock(String name, Supplier<? extends Block> source, BlockFactory<T> factory) {
    ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, ID(name));
    return register(Registries.BLOCK, name, () -> factory.create(key, BlockBehaviour.Properties.ofFullCopy(source.get()).setId(key)));
  }

  /* Example: reg.blockItem("tin_block", TIN_BLOCK) */
  public Entry<BlockItem> blockItem(String name, Supplier<? extends Block> block) {
    ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, ID(name));
    return register(Registries.ITEM, name, () -> new BlockItem(block.get(), new Item.Properties().useBlockDescriptionPrefix().setId(key)));
  }

  /* Example: reg.blockItem("special_block", SPECIAL_BLOCK, (block, props) -> new BlockItem(block, props.stacksTo(1))) */
  public <B extends Block, I extends Item> Entry<I> blockItem(String name, Supplier<? extends B> block, BlockItemFactory<B, I> factory) {
    ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, ID(name));
    return register(Registries.ITEM, name, () -> factory.create(block.get(), new Item.Properties().useBlockDescriptionPrefix().setId(key)));
  }

  /* Example: reg.blockWithItem("marble") */
  public BlockEntry<Block, BlockItem> blockWithItem(String name) {
    Entry<Block> block = block(name);
    return new BlockEntry<>(block, blockItem(name, block));
  }

  /* Example: reg.blockWithItem("marble", (key, props) -> new Block(props.strength(3.0F)), BlockItem::new) */
  public <B extends Block, I extends Item> BlockEntry<B, I> blockWithItem(String name, BlockFactory<B> blockFactory, BlockItemFactory<B, I> itemFactory) {
    Entry<B> block = block(name, blockFactory);
    return new BlockEntry<>(block, blockItem(name, block, itemFactory));
  }

  /* Example: reg.sound("sword_clang") */
  public Entry<SoundEvent> sound(String name) {
    return register(Registries.SOUND_EVENT, name, () -> SoundEvent.createVariableRangeEvent(ID(name)));
  }

  /* Example: reg.tab("metals", () -> new ItemStack(TIN_NUGGET.get())) */
  public Entry<CreativeModeTab> tab(String name, Supplier<ItemStack> icon) {
    return tab(name, Component.translatable("itemGroup." + modId + "." + name), icon);
  }

  /* Example: reg.tab("metals", Component.literal("Metal Objects"), () -> new ItemStack(TIN_NUGGET.get())) */
  public Entry<CreativeModeTab> tab(String name, Component title, Supplier<ItemStack> icon) {
    return register(Registries.CREATIVE_MODE_TAB, name, () -> new CreativeModeTab.Builder(CreativeModeTab.Row.TOP, 0).title(title).icon(icon).build());
  }

  /**
   * ==========[ Loader Hooks ]==========
   * Called by mod loader entrypoints.
   */

  // Call from Fabric's onInitialize() immediately after ExampleModCommon.init().
  public void registerAllStatic() {
    for (Registrar<?> registrar : registrars.values()) {
      registrar.registerBuiltin();
    }
    verifyAllStaticRegistrations();
  }

  // Call from NeoForge main setup by wiring a RegisterEvent listener with eventBus.addListener(this::registerRegistries), then call MainRegistry.reg.registerNeoForge(event.getRegistry()) there.
  public void registerNeoForge(Registry<?> registry) {
    Registrar<?> registrar = registrars.get(registry.key());
    if (registrar != null) {
      registrar.registerInto(registry);
    }
  }

  // Call from NeoForge main setup by wiring an FMLCommonSetupEvent listener with eventBus.addListener(this::verifyRegistriesRegistered), then call MainRegistry.reg.verifyAllStaticRegistrations() there.
  public void verifyAllStaticRegistrations() {
    Set<ResourceKey<? extends Registry<?>>> missing = new LinkedHashSet<>();
    for (Registrar<?> registrar : registrars.values()) {
      if (!registrar.committed) {
        missing.add(registrar.key);
      }
    }
    if (!missing.isEmpty()) {
      throw new IllegalStateException("Static registration did not complete for registries: " + missing);
    }
  }

  /**
   * ==========[ Registration Internals ]==========
   * Shouldn't need to touch this
   */

  @SuppressWarnings("unchecked")
  private <T> Registrar<T> registrar(ResourceKey<? extends Registry<T>> registryKey) {
    Registrar<?> existing = registrars.get(registryKey);
    if (existing != null) {
      return (Registrar<T>) existing;
    }

    Registrar<T> created = new Registrar<>(registryKey, builtinRegistry(registryKey));
    registrars.put(registryKey, created);
    return created;
  }

  private static <T> Supplier<T> memoize(Supplier<T> supplier) {
    return new Supplier<>() {
      private T value;
      private boolean resolved;

      @Override
      public T get() {
        if (!resolved) {
          value = Objects.requireNonNull(supplier.get(), "Registry factories must not return null");
          resolved = true;
        }
        return value;
      }
    };
  }

  @FunctionalInterface
  public interface ItemFactory<T extends Item> {
    T create(ResourceKey<Item> key, Item.Properties properties);
  }

  @FunctionalInterface
  public interface BlockFactory<T extends Block> {
    T create(ResourceKey<Block> key, BlockBehaviour.Properties properties);
  }

  @FunctionalInterface
  public interface BlockItemFactory<B extends Block, I extends Item> {
    I create(B block, Item.Properties properties);
  }

  /**
   * ==========[ Registration Handles ]==========
   * Define object as Entry for all types except a block with its block item. For that use BlockEntry.
   */

  /* Example: public static final KhazReg.Entry<Item> TIN_NUGGET = reg.item("tin_nugget") */
  public static final class Entry<T> implements Supplier<T> {
    private final Identifier id;
    private final ResourceKey<T> key;
    private final Supplier<T> supplier;

    private Entry(Identifier id, ResourceKey<T> key, Supplier<T> supplier) {
      this.id = Objects.requireNonNull(id, "id");
      this.key = Objects.requireNonNull(key, "key");
      this.supplier = Objects.requireNonNull(supplier, "supplier");
    }

    public Identifier id() {
      return id;
    }

    public ResourceKey<T> key() {
      return key;
    }

    @Override
    public T get() {
      return supplier.get();
    }

    @Override
    public String toString() {
      return "Entry[" + id + "]";
    }
  }

  /* Example: public static final KhazReg.BlockEntry<Block, BlockItem> MARBLE = reg.blockWithItem("marble") */
  public static class BlockEntry<B extends Block, I extends Item> implements Supplier<B> {
    private final Entry<B> block;
    private final Entry<I> item;

    protected BlockEntry(Entry<B> block, Entry<I> item) {
      this.block = block;
      this.item = item;
    }

    public Entry<B> block() {
      return block;
    }

    public Entry<I> item() {
      return item;
    }

    @Override
    public B get() {
      return block.get();
    }

    @Override
    public String toString() {
      return "BlockEntry[" + block.id() + "]";
    }
  }

  // Flag to mark registry as frozen
  public void freeze() {
    frozen = true;
  }

  private final class Registrar<T> {
    private final ResourceKey<? extends Registry<T>> key;
    private final Registry<T> builtinRegistry;
    private final Map<Identifier, Entry<?>> entries = new LinkedHashMap<>();
    private boolean committed;

    private Registrar(ResourceKey<? extends Registry<T>> key, Registry<T> builtinRegistry) {
      this.key = key;
      this.builtinRegistry = builtinRegistry;
    }

    private <T2 extends T> Entry<T2> register(String name, Supplier<T2> supplier) {
      return register(name, key -> supplier.get());
    }

    private <T2 extends T> Entry<T2> register(String name, Function<ResourceKey<T2>, T2> factory) {
      Identifier id = ID(name);
      if (frozen) {
        throw new IllegalStateException("Can't register " + id + " after registration has started. Load registry classes from MainRegistry.init() first.");
      }
      if (entries.containsKey(id)) {
        throw new IllegalArgumentException("Can't register " + id + " twice");
      }

      ResourceKey<T2> key = entryKey(id);
      Entry<T2> entry = new Entry<>(id, key, memoize(() -> factory.apply(key)));
      entries.put(id, entry);
      return entry;
    }

    private void registerBuiltin() {
      registerInto(builtinRegistry);
    }

    private void registerInto(Registry<?> registry) {
      if (committed) {
        return;
      }
      @SuppressWarnings("unchecked") Registry<Object> writable = (Registry<Object>) registry;
      for (Entry<?> entry : entries.values()) {
        Registry.register(writable, entry.id(), entry.get());
      }
      committed = true;
    }

    @SuppressWarnings("unchecked")
    private <T2 extends T> ResourceKey<T2> entryKey(Identifier id) {
      return ResourceKey.create((ResourceKey<? extends Registry<T2>>) key, id);
    }
  }
}
