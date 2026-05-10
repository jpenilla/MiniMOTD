package xyz.jpenilla.minimotd.common.config;

import java.util.List;
import org.jspecify.annotations.NonNull;
import xyz.jpenilla.minimotd.common.model.MOTD;
import xyz.jpenilla.minimotd.common.model.timerange.TimeRange;
import xyz.jpenilla.minimotd.common.model.timerange.TimeRangeFactory;

public final class MOTDRepository {
  private final List<? extends MOTD> motds;

  public MOTDRepository(final @NonNull List<? extends MOTD> motds) {
    this.motds = motds;
  }

  @NonNull
  public List<? extends MOTD> motds() {
    return motds;
  }


  @NonNull
  static MOTDRepository fromConfig(final MOTDConfig config) {
    if (config.motds().isEmpty()) {
      return new MOTDRepository(List.of());
    }

    final List<? extends MOTD> motds = config.motds().stream()
      .map(MOTDAdapter::new)
      .toList();

    if (!motds.isEmpty() && motds.stream().noneMatch(motd -> motd.timeRanges().isEmpty())) {
      // we have to request have at least one default MOTD until there is no algorithm which ensure that existing time ranges cover any time
      throw new IllegalStateException("Required at least one MOTD without time range to have a fallback");
    }

    return new MOTDRepository(motds);
  }

  private static final class MOTDAdapter implements MOTD {
    private final MOTDConfig.MOTD delegate;
    private final List<TimeRange> timeRanges;

    public MOTDAdapter(MOTDConfig.MOTD delegate) {
      this.delegate = delegate;
      this.timeRanges = delegate.getScheduleSettings().getTimeSchedule().stream()
        .map(timeRangeConfig -> TimeRangeFactory.createTimeRange(timeRangeConfig.getFrom(), timeRangeConfig.getTo()))
        .toList();
    }

    @Override
    public @NonNull String line1() {
      return delegate.line1();
    }

    @Override
    public @NonNull String line2() {
      return delegate.line2();
    }

    @Override
    public @NonNull String icon() {
      return delegate.icon();
    }

    @Override
    public @NonNull List<TimeRange> timeRanges() {
      return timeRanges;
    }

    @Override
    public int weight() {
      return delegate.getScheduleSettings().getWeight();
    }

    @Override
    public int priority() {
      return delegate.getScheduleSettings().getPriority();
    }
  }
}
