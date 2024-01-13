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
package xyz.jpenilla.minimotd.common;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import xyz.jpenilla.minimotd.common.config.PluginSettings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class VirtualHostMatchingTest {
  @Test
  void testMatching() {
    final Map<String, String> map = new LinkedHashMap<>();
    map.put("abc.xyz:25565", "1");
    map.put("subdomain.abc.xyz:25565", "2");
    map.put("*.abc.xyz:25565", "3");
    map.put("abc.*.*.domain.com:25565", "4");
    map.put("*.*.*.domain.com:25565", "5");
    final PluginSettings.ProxySettings proxySettings = new PluginSettings.ProxySettings(map);
    proxySettings.processVirtualHosts();

    assertEquals("1", proxySettings.findConfigStringForHost("abc.xyz", 25565));
    assertEquals("2", proxySettings.findConfigStringForHost("subdomain.abc.xyz", 25565));
    assertEquals("3", proxySettings.findConfigStringForHost("other_subdomain.abc.xyz", 25565));
    assertEquals("4", proxySettings.findConfigStringForHost("abc.2.3.domain.com", 25565));
    assertEquals("5", proxySettings.findConfigStringForHost("1.2.3.domain.com", 25565));
    // TCPShield-mangled hostname
    assertEquals("5", proxySettings.findConfigStringForHost("1.2.3.domain.com///127.0.0.1:25565///1681016669///MGQCMAW5X13a9cF5ysnWdrBLovH4wfVu60l7eBUwWsCI8vw0cuZ9aj+UM7+y1wC//qGHngIwRsCke7eCpO99HTHOVdvZKjx9G0E51ALqRFGWH7kxMhVKYNF2IfMaT1FfbS0/D0du", 25565));
    assertNull(proxySettings.findConfigStringForHost("subdomain.subdomain.abc.xyz", 25565));
    assertNull(proxySettings.findConfigStringForHost("google.com", 25565));
  }
}
