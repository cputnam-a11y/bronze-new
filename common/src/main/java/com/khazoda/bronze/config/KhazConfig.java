package com.khazoda.bronze.config;

import com.khazoda.bronze.Constants;
import com.khazoda.bronze.platform.Services;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

public final class KhazConfig {
  private final String modId;
  private final Path file;
  private final List<Entry<?>> entries;
  private final Map<Entry<?>, Object> values = new LinkedHashMap<>();
  private boolean loaded;

  private KhazConfig(String modId, List<Entry<?>> entries) {
    this.modId = Objects.requireNonNull(modId, "modId");
    this.file = Services.PLATFORM.getConfigDirectory().resolve(modId + ".properties");
    this.entries = List.copyOf(entries);
    validateEntries(modId, this.entries);
  }

  public static KhazConfig of(String modId, Entry<?>... entries) {
    return new KhazConfig(modId, List.of(entries));
  }

  public static Entry<Boolean> bool(String key, boolean defaultValue, String comment) {
    return new Entry<>(key, defaultValue, comment, new ValueAdapter<>() {
      @Override
      public Boolean parse(String raw, Boolean fallback) {
        if ("true".equalsIgnoreCase(raw)) return true;
        if ("false".equalsIgnoreCase(raw)) return false;
        return fallback;
      }

      @Override
      public String format(Boolean value) {
        return Boolean.toString(value);
      }
    });
  }

  public static Entry<Integer> integer(String key, int defaultValue, int min, int max, String comment) {
    String fullComment = comment + " Range: " + min + "-" + max + ".";
    return new Entry<>(key, defaultValue, fullComment, new ValueAdapter<>() {
      @Override
      public Integer parse(String raw, Integer fallback) {
        try {
          return clamp(Integer.parseInt(raw), min, max);
        } catch (NumberFormatException ignored) {
          return fallback;
        }
      }

      @Override
      public String format(Integer value) {
        return Integer.toString(clamp(value, min, max));
      }
    });
  }

  public static Entry<Double> decimal(String key, double defaultValue, double min, double max, String comment) {
    String fullComment = comment + " Range: " + min + "-" + max + ".";
    return new Entry<>(key, defaultValue, fullComment, new ValueAdapter<>() {
      @Override
      public Double parse(String raw, Double fallback) {
        try {
          return clamp(Double.parseDouble(raw), min, max);
        } catch (NumberFormatException ignored) {
          return fallback;
        }
      }

      @Override
      public String format(Double value) {
        return Double.toString(clamp(value, min, max));
      }
    });
  }

  public static Entry<String> string(String key, String defaultValue, String comment) {
    return new Entry<>(key, defaultValue, comment, new ValueAdapter<>() {
      @Override
      public String parse(String raw, String fallback) {
        return raw;
      }

      @Override
      public String format(String value) {
        return value;
      }
    });
  }

  public synchronized void load() {
    if (loaded) return;

    Properties properties = new Properties();
    if (Files.exists(file)) {
      try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
        properties.load(reader);
      } catch (IOException e) {
        Constants.LOG.warn("Failed to read config file {}: {}", file, e.getMessage());
      }
    }

    boolean changed = !Files.exists(file);
    values.clear();
    for (Entry<?> entry : entries) {
      Object value = readValue(entry, properties.getProperty(entry.key()));
      values.put(entry, value);
      String serialized = formatValue(entry, value);
      if (!Objects.equals(properties.getProperty(entry.key()), serialized)) {
        changed = true;
      }
    }

    loaded = true;
    if (changed) {
      write();
    }
  }

  public synchronized void reload() {
    loaded = false;
    load();
  }

  public synchronized <T> T get(Entry<T> entry) {
    load();
    @SuppressWarnings("unchecked")
    T value = (T) values.getOrDefault(entry, entry.defaultValue());
    return value;
  }

  private synchronized void write() {
    try {
      Files.createDirectories(file.getParent());
      Files.writeString(file, render(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      Constants.LOG.warn("Failed to write config file {}: {}", file, e.getMessage());
    }
  }

  private String render() {
    StringBuilder builder = new StringBuilder();
    builder.append("# ").append(modId).append(" config").append('\n');
    builder.append("# Delete any key to restore its default value.").append('\n').append('\n');

    for (Entry<?> entry : entries) {
      if (!entry.comment().isBlank()) {
        for (String line : splitCommentLines(entry.comment())) {
          builder.append("# ").append(line).append('\n');
        }
      }
      builder.append(entry.key()).append('=').append(formatValue(entry, values.getOrDefault(entry, entry.defaultValue()))).append('\n').append('\n');
    }
    return builder.toString();
  }

  private static List<String> splitCommentLines(String text) {
    return List.of(text.split("\\R"));
  }

  private static void validateEntries(String modId, List<Entry<?>> entries) {
    Set<String> seenKeys = new HashSet<>();
    for (Entry<?> entry : entries) {
      if (!seenKeys.add(entry.key())) {
        throw new IllegalArgumentException("Duplicate config key '" + entry.key() + "' in " + modId);
      }
    }
  }

  private static int clamp(int value, int min, int max) {
    return Math.max(min, Math.min(max, value));
  }

  private static double clamp(double value, double min, double max) {
    return Math.max(min, Math.min(max, value));
  }

  private static <T> T readValue(Entry<T> entry, String raw) {
    if (raw == null) {
      return entry.defaultValue();
    }
    return entry.adapter().parse(raw.trim(), entry.defaultValue());
  }

  private static <T> String formatValue(Entry<T> entry, Object value) {
    @SuppressWarnings("unchecked")
    T typedValue = (T) value;
    return entry.adapter().format(typedValue);
  }

  public record Entry<T>(String key, T defaultValue, String comment, ValueAdapter<T> adapter) {
    public Entry {
      Objects.requireNonNull(key, "key");
      Objects.requireNonNull(defaultValue, "defaultValue");
      Objects.requireNonNull(comment, "comment");
      Objects.requireNonNull(adapter, "adapter");
    }
  }

  public interface ValueAdapter<T> {
    T parse(String raw, T fallback);
    String format(T value);
  }
}
