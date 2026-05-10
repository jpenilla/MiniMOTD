package xyz.jpenilla.minimotd.common.model.timerange;

import java.time.OffsetDateTime;
import org.jspecify.annotations.NonNull;

public interface TimeRange {
  /**
   * Intentionally designed to be aware about timezone to be able to support functionality of relying on user time zone in future
   */
  boolean isInTimeRange(@NonNull final OffsetDateTime now);
}
