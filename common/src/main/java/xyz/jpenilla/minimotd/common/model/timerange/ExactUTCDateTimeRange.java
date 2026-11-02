package xyz.jpenilla.minimotd.common.model.timerange;

import org.jspecify.annotations.NonNull;
import java.time.*;

final class ExactUTCDateTimeRange extends AbstractTimeRange<Instant> {

  ExactUTCDateTimeRange(final @NonNull Instant fromUTC, final @NonNull Instant toUTC) {
    super(fromUTC, toUTC);
  }

  @Override
  public boolean isInTimeRange(final @NonNull OffsetDateTime now) {
    final var nowUTC = now.toInstant();

    return (from.isBefore(nowUTC) || from.equals(nowUTC))
      && (to.isAfter(nowUTC) || to.equals(nowUTC));
  }
}
