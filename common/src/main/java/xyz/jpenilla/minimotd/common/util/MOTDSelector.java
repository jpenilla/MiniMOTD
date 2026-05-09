package xyz.jpenilla.minimotd.common.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import xyz.jpenilla.minimotd.common.config.MOTDConfig;
import xyz.jpenilla.minimotd.common.model.MOTD;

public final class MOTDSelector {
  private static List<? extends MOTD> filterInTimeRange(final List<? extends MOTD> motds) {
    final OffsetDateTime now = OffsetDateTime.now(ZoneId.of("UTC"));

    final List<? extends MOTD> motdsInCurrentTimeRange = motds.stream()
      .filter(motd -> motd.timeRanges().isEmpty()
        || motd.timeRanges().stream()
        .anyMatch(timeRange -> timeRange.isInTimeRange(now)))
      .toList();

    if (motdsInCurrentTimeRange.isEmpty()) {
      throw new IllegalStateException("MOTD is enabled, but there are no MOTDs for current time range. Probably you should define at least one default");
    }

    return motdsInCurrentTimeRange;
  }

  private static List<? extends MOTD> filterByPriority(final List<? extends MOTD> motds) {
    if (motds.size() <= 1) {
      return motds;
    }

    final int highestPriority = motds.stream()
      .mapToInt(MOTD::priority)
      .min()
      .getAsInt();

    return motds.stream()
      .filter(motd -> motd.priority() == highestPriority)
      .toList();
  }

  private static MOTD selectByWeight(final List<? extends MOTD> motds) {
    if (motds.isEmpty()) {
      throw new IllegalArgumentException("MOTDs expected to be not empty");
    }

    if (motds.size() == 1) {
      return motds.get(0);
    }

    final long weightSum = motds.stream()
      .mapToLong(MOTD::weight)
      .sum();

    long stopWeightCount = ThreadLocalRandom.current().nextLong(weightSum);

    for (final MOTD motd : motds) {
      stopWeightCount -= motd.weight();
      if (stopWeightCount <= 0) {
        return motd;
      }
    }

    // Fallback, but expected to be not reachable
    return motds.stream().max(Comparator.comparing(MOTD::weight)).orElseThrow();
  }


  public static @NonNull MOTD select(final @NotNull List<? extends MOTD> motds) {
    if (motds.isEmpty()) {
      throw new IllegalArgumentException("MOTDs expected to be not empty");
    }

    final var inTimeRange = filterInTimeRange(motds);

    final var inPriority = filterByPriority(inTimeRange);

    return selectByWeight(inPriority);
  }
}
