package xyz.jpenilla.minimotd.common.model.timerange;

import org.jspecify.annotations.NonNull;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

final class OnlyUTCTimeRange extends AbstractTimeRange<LocalTime> {

  OnlyUTCTimeRange(final @NonNull LocalTime from, final @NonNull LocalTime to) {
    super(from, to);
  }

  @Override
  public boolean isInTimeRange(@NonNull OffsetDateTime now) {
    final LocalTime nowTimeUTC = now.withOffsetSameInstant(ZoneOffset.UTC).toLocalTime();

    return (from.isBefore(nowTimeUTC) || from.equals(nowTimeUTC))
      && (to.isAfter(nowTimeUTC) || to.equals(nowTimeUTC));
  }
}
