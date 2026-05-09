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
package xyz.jpenilla.minimotd.common.util;

import java.util.List;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import xyz.jpenilla.minimotd.common.model.MOTD;
import xyz.jpenilla.minimotd.common.model.timerange.TimeRange;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MOTDSelectorTest {
  private static final TimeRange ALWAYS = now -> true;
  private static final TimeRange NEVER = now -> false;

  @Test
  void select_emptyList_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> MOTDSelector.select(List.of()));
  }

  @Test
  void select_allOutOfTimeRange_throwsIllegalStateException() {
    final List<MOTD> motds = List.of(
      motd("a", List.of(NEVER), 1, 0),
      motd("b", List.of(NEVER), 1, 0)
    );
    final IllegalStateException ex = assertThrows(IllegalStateException.class, () -> MOTDSelector.select(motds));
    assertTrue(ex.getMessage().contains("no MOTDs for current time range"));
  }

  @Test
  void select_singleMotdWithNoTimeRanges_returnsIt() {
    final TestMOTD only = motd("only", List.of(), 1, 0);
    assertSame(only, MOTDSelector.select(List.of(only)));
  }

  @Test
  void select_distinctPriorities_keepsLowestNumericPriority() {
    final TestMOTD best = motd("best", List.of(), 1, 1);
    final List<MOTD> motds = List.of(
      motd("mid", List.of(), 1, 5),
      best,
      motd("low", List.of(), 1, 10)
    );
    assertSame(best, MOTDSelector.select(motds));
  }

  @Test
  void select_oneActiveOneInactive_returnsActive() {
    final TestMOTD active = motd("active", List.of(), 1, 0);
    final List<MOTD> motds = List.of(
      active,
      motd("inactive", List.of(NEVER), 1, 0)
    );
    assertSame(active, MOTDSelector.select(motds));
  }

  @Test
  void select_anyTimeRangeMatch_isActive() {
    final TestMOTD orRanges = motd("or", List.of(NEVER, ALWAYS), 1, 0);
    assertSame(orRanges, MOTDSelector.select(List.of(orRanges)));
  }

  @Test
  void select_samePriorityAfterFilters_returnsOneOfCandidates() {
    final TestMOTD first = motd("first", List.of(), 1, 0);
    final TestMOTD second = motd("second", List.of(), 1, 0);
    final MOTD result = MOTDSelector.select(List.of(first, second));
    assertTrue(result == first || result == second);
  }

  private static TestMOTD motd(
    final String id,
    final List<TimeRange> timeRanges,
    final int weight,
    final int priority
  ) {
    return new TestMOTD(id, timeRanges, weight, priority);
  }

  private static final class TestMOTD implements MOTD {
    private final String id;
    private final List<TimeRange> timeRanges;
    private final int weight;
    private final int priority;

    private TestMOTD(
      final String id,
      final List<TimeRange> timeRanges,
      final int weight,
      final int priority
    ) {
      this.id = id;
      this.timeRanges = List.copyOf(timeRanges);
      this.weight = weight;
      this.priority = priority;
    }

    @Override
    public @NonNull String line1() {
      return this.id;
    }

    @Override
    public @NonNull String line2() {
      return "";
    }

    @Override
    public @NonNull String icon() {
      return "";
    }

    @Override
    public @NonNull List<TimeRange> timeRanges() {
      return this.timeRanges;
    }

    @Override
    public int weight() {
      return this.weight;
    }

    @Override
    public int priority() {
      return this.priority;
    }
  }
}
