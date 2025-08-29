/*
 * This file is part of MiniMOTD, licensed under the MIT License.
 *
 * Copyright (c) 2020-2024 Jason Penilla
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class Nag {
  private static final String EQUALS_LINE = "====================================================";

  private final List<String> lines = new ArrayList<>();
  private boolean exception = false;
  private boolean error = false;

  public Nag lines(final String... lines) {
    this.lines.addAll(Arrays.asList(lines));
    return this;
  }

  public Nag line(final String line) {
    this.lines.add(line);
    return this;
  }

  public Nag error(final boolean withException) {
    this.error = true;
    this.exception = withException;
    return this;
  }

  protected abstract void logError(String message, @Nullable Throwable throwable);

  protected abstract void logWarn(String message, @Nullable Throwable throwable);

  public void logBanner() {
    final List<String> toLog = new ArrayList<>();
    toLog.add(EQUALS_LINE);
    toLog.addAll(this.lines);
    toLog.add(EQUALS_LINE);

    for (final String line : toLog) {
      if (this.error) {
        this.logError(line, null);
      } else {
        this.logWarn(line, null);
      }
    }

    if (this.exception) {
      // this.exception implies this.error
      this.logError("Please see the above notice.", new RuntimeException("Please see the above notice."));
    }
  }

  public static final class Slf4J extends Nag {
    private final org.slf4j.Logger logger;

    public Slf4J(final org.slf4j.Logger logger) {
      this.logger = logger;
    }

    @Override
    protected void logError(final String message, @Nullable final Throwable throwable) {
      if (throwable != null) {
        this.logger.error(message, throwable);
      } else {
        this.logger.error(message);
      }
    }

    @Override
    protected void logWarn(final String message, @Nullable final Throwable throwable) {
      if (throwable != null) {
        this.logger.warn(message, throwable);
      } else {
        this.logger.warn(message);
      }
    }
  }

  public static final class JavaUtilLogging extends Nag {
    private final java.util.logging.Logger logger;

    public JavaUtilLogging(final java.util.logging.Logger logger) {
      this.logger = logger;
    }

    @Override
    protected void logError(final String message, @Nullable final Throwable throwable) {
      if (throwable != null) {
        this.logger.log(java.util.logging.Level.SEVERE, message, throwable);
      } else {
        this.logger.severe(message);
      }
    }

    @Override
    protected void logWarn(final String message, @Nullable final Throwable throwable) {
      if (throwable != null) {
        this.logger.log(java.util.logging.Level.WARNING, message, throwable);
      } else {
        this.logger.warning(message);
      }
    }
  }
}
