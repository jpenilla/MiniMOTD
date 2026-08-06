package xyz.jpenilla.minimotd.common.model;

import org.jspecify.annotations.NonNull;
import xyz.jpenilla.minimotd.common.model.timerange.TimeRange;
import java.util.List;

public interface MOTD {
  @NonNull
  String line1();

  @NonNull
  String line2();

  @NonNull
  String icon();

  @NonNull
  List<TimeRange> timeRanges();

  int weight();

  int priority();
}
