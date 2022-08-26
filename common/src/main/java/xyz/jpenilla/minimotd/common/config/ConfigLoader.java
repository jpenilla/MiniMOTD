/*
 * This file is part of MiniMOTD, licensed under the MIT License.
 *
 * Copyright (c) 2020-2022 Jason Penilla
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package xyz.jpenilla.minimotd.common.config;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

@DefaultQualifier(NonNull.class)
public final class ConfigLoader<C> {
  private static final TypeSerializerCollection SERIALIZERS;

  static {
    SERIALIZERS = TypeSerializerCollection.builder()
      .register(PlayerCountModifier.serializer())
      .build();
  }

  private final HoconConfigurationLoader loader;
  private final ObjectMapper<C> mapper;
  private final List<Consumer<C>> postProcess;

  public ConfigLoader(
    final Class<C> configClass,
    final Path configPath,
    final UnaryOperator<ConfigurationOptions> optionsModifier
  ) {
    this.postProcess = this.findPostProcessors(configClass);
    this.loader = HoconConfigurationLoader.builder()
      .path(configPath)
      .defaultOptions(options ->
        optionsModifier.apply(options)
          .serializers(builder -> builder.registerAll(SERIALIZERS)))
      .build();
    try {
      this.mapper = ObjectMapper.factory().get(configClass);
    } catch (final SerializationException ex) {
      throw new IllegalStateException(
        "Failed to initialize an object mapper for type: " + configClass.getSimpleName(),
        ex
      );
    }
  }

  private List<Consumer<C>> findPostProcessors(final Class<C> configClass) {
    final List<Consumer<C>> ret = new ArrayList<>();
    for (final Method method : configClass.getDeclaredMethods()) {
      if (method.getAnnotation(PostProcessor.class) == null) {
        continue;
      }
      ret.add(config -> {
        try {
          method.setAccessible(true);
          method.invoke(config);
        } catch (final ReflectiveOperationException ex) {
          throw new RuntimeException("Failed to invoke post processor method", ex);
        }
      });
    }
    return ret;
  }

  public ConfigLoader(
    final @NonNull Class<C> configClass,
    final @NonNull Path configPath
  ) {
    this(configClass, configPath, options -> options);
  }

  public @NonNull C load() throws ConfigurateException {
    final CommentedConfigurationNode node = this.loader.load();
    final C config = this.mapper.load(node);
    for (final Consumer<C> processor : this.postProcess) {
      processor.accept(config);
    }
    return config;
  }

  public void save(final @NonNull C config) throws ConfigurateException {
    final CommentedConfigurationNode node = this.loader.createNode();
    this.mapper.save(config, node);
    this.loader.save(node);
  }
}
