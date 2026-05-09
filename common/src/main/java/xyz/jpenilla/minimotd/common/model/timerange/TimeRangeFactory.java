package xyz.jpenilla.minimotd.common.model.timerange;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import org.jspecify.annotations.NonNull;

public final class TimeRangeFactory {

  private static final DateTimeFormatter ISO_LOCAL_DATE_TIME_OPTIONAL_OFFSET = new DateTimeFormatterBuilder()
    .append(DateTimeFormatter.ISO_LOCAL_DATE)
    .appendLiteral('T')
    .append(DateTimeFormatter.ISO_LOCAL_TIME)
    .optionalStart()
    .appendOffsetId()
    .optionalEnd()
    .toFormatter();

  private static final DateTimeFormatter ISO_LOCAL_TIME_OPTIONAL_OFFSET = new DateTimeFormatterBuilder()
    .append(DateTimeFormatter.ISO_LOCAL_TIME)
    .optionalStart()
    .appendOffsetId()
    .optionalEnd()
    .toFormatter();

  @NonNull
  public static TimeRange createTimeRange(final @NonNull String fromRaw, final @NonNull String toRaw) throws TimeRangeException {
    final String from = fromRaw.trim();
    final String to = toRaw.trim();

    final boolean fromDateTime = hasIsoDateAndTimeSeparator(from);
    final boolean toDateTime = hasIsoDateAndTimeSeparator(to);
    if (fromDateTime != toDateTime) {
      throw new TimeRangeException("`from` and `to` must both use a full date-time or both use time-of-day only");
    }

    if (fromDateTime) {
      return new ExactUTCDateTimeRange(parseInstantUtc(from), parseInstantUtc(to));
    }

    if (looksLikeTimeOfDay(from) && looksLikeTimeOfDay(to)) {
      return new OnlyUTCTimeRange(parseUtcLocalTime(from), parseUtcLocalTime(to));
    }

    throw new TimeRangeException("Unrecognized time range format: [%s, %s]".formatted(fromRaw, toRaw));
  }

  private static boolean hasIsoDateAndTimeSeparator(final String s) {
    return s.length() >= 11
      && s.charAt(4) == '-'
      && s.charAt(7) == '-'
      && s.charAt(10) == 'T';
  }

  private static boolean looksLikeTimeOfDay(final String s) {
    return s.length() >= 5
      && Character.isDigit(s.charAt(0))
      && Character.isDigit(s.charAt(1))
      && s.charAt(2) == ':'
      && Character.isDigit(s.charAt(3))
      && Character.isDigit(s.charAt(4));
  }

  private static @NonNull Instant parseInstantUtc(final String s) {
    final TemporalAccessor parsed;
    try {
      parsed = ISO_LOCAL_DATE_TIME_OPTIONAL_OFFSET.parse(s);
    } catch (final DateTimeParseException e) {
      throw new TimeRangeException("Invalid date-time: " + s);
    }
    final LocalDate date = LocalDate.from(parsed);
    final LocalTime time = LocalTime.from(parsed);
    if (parsed.isSupported(ChronoField.OFFSET_SECONDS)) {
      return OffsetDateTime.of(date, time, ZoneOffset.ofTotalSeconds(parsed.get(ChronoField.OFFSET_SECONDS)))
        .toInstant();
    }
    return LocalDateTime.of(date, time).toInstant(ZoneOffset.UTC);
  }

  private static @NonNull LocalTime parseUtcLocalTime(final String s) {
    final TemporalAccessor parsed;
    try {
      parsed = ISO_LOCAL_TIME_OPTIONAL_OFFSET.parse(s);
    } catch (final DateTimeParseException e) {
      throw new TimeRangeException("Invalid time-of-day: " + s);
    }
    final LocalTime local = LocalTime.from(parsed);
    if (parsed.isSupported(ChronoField.OFFSET_SECONDS)) {
      return OffsetTime.of(local, ZoneOffset.ofTotalSeconds(parsed.get(ChronoField.OFFSET_SECONDS)))
        .withOffsetSameInstant(ZoneOffset.UTC)
        .toLocalTime();
    }
    return local;
  }
}
