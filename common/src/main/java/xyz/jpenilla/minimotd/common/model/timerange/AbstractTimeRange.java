package xyz.jpenilla.minimotd.common.model.timerange;

import org.jspecify.annotations.NonNull;

abstract class AbstractTimeRange<UNIT extends Comparable<UNIT>> implements TimeRange {
  @NonNull
  protected final UNIT from;

  @NonNull
  protected final UNIT to;

  AbstractTimeRange(final @NonNull UNIT from, final @NonNull UNIT to) {
    if (from.compareTo(to) > 0) {
      throw new TimeRangeException("Invalid time range: [%s, %s]".formatted(from, to));
    }

    this.from = from;
    this.to = to;
  }

  public final @NonNull UNIT from() {
    return from;
  }

  public final @NonNull UNIT to() {
    return to;
  }
}
