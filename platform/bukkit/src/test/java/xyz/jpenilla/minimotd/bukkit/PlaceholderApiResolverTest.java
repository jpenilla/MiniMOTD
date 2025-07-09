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
package xyz.jpenilla.minimotd.bukkit;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class PlaceholderApiResolverTest {
  private PlaceholderApiResolver resolver;

  @BeforeEach
  void setup() {
    this.resolver = new PlaceholderApiResolver();
  }

  @Test
  void testParseWithPlayerAndPlaceholderAPIEnabled() {
    final Player player = mock(Player.class);

    try (
      MockedStatic<Bukkit> bukkitMock = mockStatic(Bukkit.class);
      MockedStatic<PlaceholderAPI> placeholderApiMock = mockStatic(PlaceholderAPI.class)
    ) {
      final PluginManager pluginManager = mock(PluginManager.class);
      bukkitMock.when(Bukkit::getPluginManager).thenReturn(pluginManager);
      when(pluginManager.isPluginEnabled("PlaceholderAPI")).thenReturn(true);
      placeholderApiMock.when(() -> PlaceholderAPI.setPlaceholders(player, "%test%")).thenReturn("resolved-player");

      // The resolver should now call PlaceholderAPI.setPlaceholders(player, input)
      final String result = this.resolver.parse("%test%", player);

      assertEquals("resolved-player", result);
    }
  }

  @Test
  void testParseWithNonPlayerAndPlaceholderAPIEnabled() {
    try (
      MockedStatic<Bukkit> bukkitMock = mockStatic(Bukkit.class);
      MockedStatic<PlaceholderAPI> placeholderApiMock = mockStatic(PlaceholderAPI.class)
    ) {
      final PluginManager pluginManager = mock(PluginManager.class);
      bukkitMock.when(Bukkit::getPluginManager).thenReturn(pluginManager);
      when(pluginManager.isPluginEnabled("PlaceholderAPI")).thenReturn(true);
      placeholderApiMock.when(() -> PlaceholderAPI.setPlaceholders(null, "%test%")).thenReturn("resolved-server");

      final String result = this.resolver.parse("%test%", new Object());

      assertEquals("resolved-server", result);
    }
  }

  @Test
  void testParseWithPlaceholderAPIDisabled() {
    final Player player = mock(Player.class);

    try (MockedStatic<Bukkit> bukkitMock = mockStatic(Bukkit.class)) {
      final PluginManager pluginManager = mock(PluginManager.class);
      bukkitMock.when(Bukkit::getPluginManager).thenReturn(pluginManager);
      when(pluginManager.isPluginEnabled("PlaceholderAPI")).thenReturn(false);

      final String result = this.resolver.parse("%test%", player);

      assertEquals("%test%", result);
    }
  }
}
