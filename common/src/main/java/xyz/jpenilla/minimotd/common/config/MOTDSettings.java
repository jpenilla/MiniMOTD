package xyz.jpenilla.minimotd.common.config;

import org.jspecify.annotations.NonNull;

public final class MOTDSettings {
  private final MOTDConfig motdConfig;
  private final MOTDRepository motdRepository;

  public MOTDSettings(final @NonNull MOTDConfig motdConfig) {
    this.motdConfig = motdConfig;
    this.motdRepository = MOTDRepository.fromConfig(motdConfig);
  }

  public @NonNull MOTDConfig getMotdConfig() {
    return motdConfig;
  }

  public @NonNull MOTDRepository getMotdRepository() {
    return motdRepository;
  }
}
