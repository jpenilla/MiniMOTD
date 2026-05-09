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
package xyz.jpenilla.minimotd.common.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.spongepowered.configurate.ConfigurateException;
import xyz.jpenilla.minimotd.common.model.MOTD;
import xyz.jpenilla.minimotd.common.model.timerange.TimeRange;
import xyz.jpenilla.minimotd.common.model.timerange.TimeRangeException;
import xyz.jpenilla.minimotd.common.util.MOTDSelector;

import static org.junit.jupiter.api.Assertions.*;

class MOTDTimeRangeConfigIntegrationTest {

  @Test
  void exactUtcDateTimeRange_fromHocon(@TempDir final Path tempDir) throws IOException {
    final Path file = copyFixture(tempDir, "motd-timerange-exact.conf");
    final MOTD motd = firstMotd(loadMotdConfig(file));
    final TimeRange range = motd.timeRanges().get(0);

    assertTrue(range.isInTimeRange(OffsetDateTime.of(2024, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)));
    assertFalse(range.isInTimeRange(OffsetDateTime.of(2024, 6, 15, 9, 0, 0, 0, ZoneOffset.UTC)));
    assertFalse(range.isInTimeRange(OffsetDateTime.of(2024, 6, 15, 19, 0, 0, 0, ZoneOffset.UTC)));
  }

  @Test
  void timeOfDayOnlyRange_fromHocon(@TempDir final Path tempDir) throws IOException {
    final Path file = copyFixture(tempDir, "motd-timerange-timeofday.conf");
    final MOTD motd = firstMotd(loadMotdConfig(file));
    final TimeRange range = motd.timeRanges().get(0);

    assertTrue(range.isInTimeRange(OffsetDateTime.of(2024, 6, 16, 12, 0, 0, 0, ZoneOffset.UTC)));
    assertFalse(range.isInTimeRange(OffsetDateTime.of(2024, 6, 16, 3, 0, 0, 0, ZoneOffset.UTC)));
  }

  @Test
  void explicitOffsetParsedAsUtcInstant_fromHocon(@TempDir final Path tempDir) throws IOException {
    final Path file = copyFixture(tempDir, "motd-timerange-offset.conf");
    final MOTD motd = firstMotd(loadMotdConfig(file));
    final TimeRange range = motd.timeRanges().get(0);

    assertTrue(range.isInTimeRange(OffsetDateTime.of(2024, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)));
    assertFalse(range.isInTimeRange(OffsetDateTime.of(2024, 6, 15, 9, 0, 0, 0, ZoneOffset.UTC)));
    assertFalse(range.isInTimeRange(OffsetDateTime.of(2024, 6, 15, 19, 0, 0, 0, ZoneOffset.UTC)));
  }

  @Test
  void mixedDateTimeAndTimeOfDay_throws(@TempDir final Path tempDir) throws IOException {
    final Path file = copyFixture(tempDir, "motd-timerange-invalid-mixed.conf");
    final MOTDConfig config = loadMotdConfig(file);
    assertThrows(TimeRangeException.class, () -> new MOTDSettings(config));
  }

  @Test
  void invertedBounds_throws(@TempDir final Path tempDir) throws IOException {
    final Path file = copyFixture(tempDir, "motd-timerange-invalid-inverted.conf");
    final MOTDConfig config = loadMotdConfig(file);
    assertThrows(TimeRangeException.class, () -> new MOTDSettings(config));
  }

  @Test
  void motdSelector_withoutSchedule_respectsPriorityOverWeight(@TempDir final Path tempDir) throws IOException {
    final Path file = copyFixture(tempDir, "motd-selector-priority-no-schedule.conf");
    final var motds = new MOTDSettings(loadMotdConfig(file)).getMotdRepository().motds();
    final MOTD selected = MOTDSelector.select(motds);
    assertEquals("lower-priority-wins", selected.line1());
  }

  @Test
  void motdSelector_withoutSchedule_samePriority_respectsWeightCompetition(@TempDir final Path tempDir) throws IOException {
    final Path file = copyFixture(tempDir, "motd-selector-priority-weight-no-schedule.conf");
    final var motds = new MOTDSettings(loadMotdConfig(file)).getMotdRepository().motds();
    final MOTD selected = MOTDSelector.select(motds);
    assertTrue(
      "candidate-a".equals(selected.line1()) || "candidate-b".equals(selected.line1()),
      "expected one of the equal-priority MOTDs"
    );
  }

  @Test
  void motdSelector_withSchedule_excludesMotdOutsideCurrentUtcWindow(@TempDir final Path tempDir) throws IOException {
    final Path file = copyFixture(tempDir, "motd-selector-time-and-priority.conf");
    final var motds = new MOTDSettings(loadMotdConfig(file)).getMotdRepository().motds();
    final MOTD selected = MOTDSelector.select(motds);
    assertEquals("fallback-default-schedule", selected.line1());
  }

  @Test
  void motdSelector_withSchedule_andWithoutSchedule_picksBestPriorityAmongActive(@TempDir final Path tempDir) throws IOException {
    final Path file = copyFixture(tempDir, "motd-selector-time-wide-priority.conf");
    final var motds = new MOTDSettings(loadMotdConfig(file)).getMotdRepository().motds();
    final MOTD selected = MOTDSelector.select(motds);
    assertEquals("always-active-best-priority", selected.line1());
  }

  private static MOTDConfig loadMotdConfig(final Path path) throws ConfigurateException {
    return new ConfigLoader<>(MOTDConfig.class, path).load();
  }

  private static MOTD firstMotd(final MOTDConfig config) {
    return new MOTDSettings(config).getMotdRepository().motds().get(0);
  }

  private Path copyFixture(final Path tempDir, final String name) throws IOException {
    final String resourcePath = "/integration/" + name;
    final InputStream stream = MOTDTimeRangeConfigIntegrationTest.class.getResourceAsStream(resourcePath);
    if (stream == null) {
      throw new IllegalStateException("Missing classpath resource: " + resourcePath);
    }
    final Path dest = tempDir.resolve(name);
    try (stream) {
      Files.copy(stream, dest, StandardCopyOption.REPLACE_EXISTING);
    }
    return dest;
  }
}
