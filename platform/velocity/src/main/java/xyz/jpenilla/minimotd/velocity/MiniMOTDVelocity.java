/*
 * This file is part of MiniMOTD, licensed under the MIT License.
 *
 * Copyright (c) 2020-2025 Jason Penilla
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
package xyz.jpenilla.minimotd.velocity;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.util.Favicon;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Set;
import org.bstats.velocity.Metrics;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import xyz.jpenilla.minimotd.common.CommandHandler;
import xyz.jpenilla.minimotd.common.MiniMOTD;
import xyz.jpenilla.minimotd.common.MiniMOTDPlatform;
import xyz.jpenilla.minimotd.common.util.BrigadierUtil;
import xyz.jpenilla.minimotd.common.util.UpdateChecker;

public final class MiniMOTDVelocity implements MiniMOTDPlatform<Favicon> {
  private static final Set<Class<?>> LISTENER_CLASSES = ImmutableSet.of(
    PingListener.class
  );

  private final MiniMOTD<Favicon> miniMOTD;
  private final ProxyServer server;
  private final Logger logger;
  private final CommandManager commandManager;
  private final Path dataDirectory;
  private final Metrics.Factory metricsFactory;
  private final Injector injector;

  @Inject
  public MiniMOTDVelocity(
    final @NonNull ProxyServer server,
    final @NonNull Logger logger,
    final @NonNull CommandManager commandManager,
    @DataDirectory final @NonNull Path dataDirectory,
    final Metrics.@NonNull Factory metricsFactory,
    final @NonNull Injector injector
  ) {
    this.server = server;
    this.logger = logger;
    this.commandManager = commandManager;
    this.dataDirectory = dataDirectory;
    this.metricsFactory = metricsFactory;
    this.miniMOTD = new MiniMOTD<>(this);
    this.miniMOTD.configManager().loadExtraConfigs();
    this.injector = injector.createChildInjector(new AbstractModule() {
      @Override
      protected void configure() {
        this.bind(new TypeLiteral<MiniMOTD<Favicon>>() {
        }).toInstance(MiniMOTDVelocity.this.miniMOTD);
      }
    });
  }

  @Subscribe
  public void onProxyInitialization(final @NonNull ProxyInitializeEvent event) {
    for (final Class<?> clazz : LISTENER_CLASSES) {
      this.server.getEventManager().register(this, this.injector.getInstance(clazz));
    }
    this.registerCommand();
    this.metricsFactory.make(this, 10257);
    if (this.miniMOTD.configManager().pluginSettings().updateChecker()) {
      this.server.getScheduler().buildTask(
        this,
        () -> new UpdateChecker().checkVersion().forEach(this.logger::info)
      ).schedule();
    }
  }

  private void registerCommand() {
    this.commandManager.register(
      this.commandManager.metaBuilder("minimotd").plugin(this).build(),
      new BrigadierCommand(
        BrigadierUtil.buildTree(
          new CommandHandler(this.miniMOTD),
          source -> source,
          source -> source.hasPermission("minimotd.admin")
        )
      )
    );
  }

  @Override
  public @NonNull Path dataDirectory() {
    return this.dataDirectory;
  }

  @Override
  public @NonNull Logger logger() {
    return this.logger;
  }

  @Override
  public @NonNull Favicon loadIcon(final @NonNull BufferedImage image) {
    return Favicon.create(image);
  }

  @Override
  public void onReload() {
    this.miniMOTD.configManager().loadExtraConfigs();
  }
}
