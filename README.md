![MiniMOTD logo](resources/minimotd-logo.png)

[![build](https://img.shields.io/github/checks-status/jpenilla/MiniMOTD/master?label=build)](https://github.com/jpenilla/MiniMOTD/actions) [![latest release](https://img.shields.io/github/v/release/jpenilla/MiniMOTD)](https://github.com/jpenilla/MiniMOTD/releases)

### MiniMOTD is a basic server list MOTD plugin/mod for Minecraft servers and proxies

- MiniMOTD supports RGB colors and gradients through [MiniMessage](https://github.com/KyoriPowered/adventure-text-minimessage), which is also where MiniMOTD gets it's name.
- For more detailed info on formatting text, refer to the [MiniMessage docs](https://docs.adventure.kyori.net/minimessage.html).
- RGB colors are automatically downsampled for outdated clients.
- RGB colors are only able to be sent by proxies and 1.16+ servers, and can only be seen by 1.16+ clients.

#### Server Platforms
- [Paper](https://papermc.io/) / Spigot
  - MiniMOTD is compatible with Spigot, however many features will not work. It is recommended to use Paper for full compatibility.
- [Sponge API 8](https://www.spongepowered.org/)
- [Sponge API 7](https://www.spongepowered.org/)
- [Fabric](https://fabricmc.net/) (requires [Fabric API](https://modrinth.com/mod/fabric-api))
- [NeoForge](https://neoforged.net/)

#### Proxy Platforms
- [Velocity](https://velocitypowered.com/)
- [Waterfall](https://papermc.io/downloads#Waterfall) / Bungeecord

#### Downloads
Downloads can be obtained from any of:
 - [Modrinth](https://modrinth.com/plugin/minimotd)
 - [Hangar](https://hangar.papermc.io/jmp/MiniMOTD)
 - [GitHub releases](https://github.com/jpenilla/MiniMOTD/releases)

There is a separate jar for each platform. Paper and Spigot share the same jar, as do Waterfall and Bungeecord.
The `bukkit-bungeecord` jar is merged from the Paper/Spigot and Waterfall/Bungeecord jars for distribution on Spigot's site.

#### Configuration
See the [wiki](https://github.com/jpenilla/MiniMOTD/wiki) for configuration details

#### Screenshots
![demo motd image](resources/minimotd-demo.png)
