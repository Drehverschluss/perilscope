<p align="center">
  <img src="src/main/resources/perilscope.png" alt="Perilscope" width="160">
</p>

<h1 align="center">Perilscope</h1>

<p align="center">
  <img alt="Minecraft 1.21.1" src="https://img.shields.io/badge/Minecraft-1.21.1-5b8731">
  <img alt="NeoForge" src="https://img.shields.io/badge/NeoForge-21.1-e68c37">
  <img alt="License MIT" src="https://img.shields.io/badge/License-MIT-blue">
</p>

🔭 **Know your peril.** Perilscope is a NeoForge mod for Minecraft 1.21.1 (Java 21) that shows two freely
positionable HUD elements:

1. **Area Difficulty** – the area level of [Dynamic Difficulty](https://github.com/muon-rw/Dynamic-Difficulty) at
   the player position, optionally with the structure and biome bonus next to it.
2. **Dungeon Difficulty** – the type and level of the [Dungeon Difficulty](https://modrinth.com/mod/dungeon-difficulty)
   zone. Only visible while the player stands inside a zone, and disappears again when they leave it.

Both integrations are optional: with only one of the two mods installed, Perilscope simply shows the matching element.

_Screenshot: Area Difficulty HUD element (placeholder)_

_Screenshot: Dungeon Difficulty zone HUD element (placeholder)_

## How it works

All values are computed on the server (every 10 ticks per player) and sent to the client with the
`perilscope:difficulty_state` payload, only when they changed (plus on login and dimension change). The payload
channel is registered as optional: the HUD needs the mod on server **and** client, but clients without Perilscope can
still join a server that has it (they simply don't get the HUD).

Dungeon Difficulty has no public API. Perilscope uses its internal classes
(`net.dungeon_difficulty.logic.PatternMatching`, `Difficulty`), so an update of Dungeon Difficulty may break the
integration. On any error the query is logged once and disabled for the rest of the server session.

## Installation

1. Install NeoForge `21.1` for Minecraft `1.21.1`.
2. Put `perilscope-0.1.0.jar` into the `mods` folder of the **server and of every client** that should see the HUD.
3. Optional: install Dynamic Difficulty and/or Dungeon Difficulty to get their values and zones.

| Mod | Version | Needed |
|---|---|---|
| Dynamic Difficulty | `1.3.4+1.21.1` | optional |
| Dungeon Difficulty | `3.9.1+1.21.1` | optional |

- The server calculates the values and sends them to the clients, so Perilscope is needed on both sides to show the HUD.
- Both integrations are `compileOnly` + `localRuntime` at build time, so they are available in `runClient` /
  `runServer` but are never required by the built mod. Without them, the corresponding HUD element simply stays hidden.

## What it does

- **Area Difficulty** — the Dynamic Difficulty level at the player's position, colored by difficulty, optionally with
  the structure and biome bonus.
- **Dungeon Difficulty** — the type and level of the zone the player currently stands in. Settlements and ruins
  (configurable) are shown in green without a level, the other zones from yellow to red.
- **Six frame styles** — None, Simple, Tooltip, Bevel, Ornate and Panel, all tinted with the difficulty color; the
  semi-transparent background can be toggled on its own.
- **Drag & drop layout editor** — move, scale, hide and recolor both elements live, see below.

## Configuration

Client config `config/perilscope-client.toml` (also editable in-game via *Mods → Perilscope → Config*), per element:

| Option | Meaning |
|---|---|
| `anchor` | `TOP_LEFT`, `TOP_CENTER`, `TOP_RIGHT`, `CENTER_LEFT`, `CENTER`, `CENTER_RIGHT`, `BOTTOM_LEFT`, `BOTTOM_CENTER`, `BOTTOM_RIGHT` |
| `offsetX`, `offsetY` | GUI pixels, added to the anchor point |
| `scale` | Size of the element |
| `visible` | Show or hide the element |
| `showBackground` | Semi-transparent background behind the element |
| `frameStyle` | `NONE`, `SIMPLE`, `TOOLTIP`, `BEVEL`, `ORNATE`, `PANEL`, tinted with the difficulty color |
| `colorByDifficulty` | Shift text / frame color with the difficulty |
| `colorMaxLevel` | Level at which the hardest color is reached |

The area element additionally has `showBonuses`, the dungeon element `safeTypes` (zone types shown in green without a
level, default `settlement` and `ruins`).

## Layout editor

Open it via *Mods → Perilscope → Config → Layout & Style* or the key binding *Edit HUD Layout* (unbound by default,
*Options → Controls → Perilscope*).

- **Drag** an element to move it — it snaps to the screen edges and center, hold **Shift** to disable snapping.
- **Mouse wheel** over an element to scale, **right click** to show / hide (hidden elements are dimmed in the editor).
- Select an element to change its frame style, background, colors, visibility (and the bonuses of the area element)
  with the buttons at the bottom. The real HUD element updates immediately. `F` cycles the frame style, `B` toggles
  the background.
- **Arrow keys** nudge the selected element by 1 px (Shift: 10 px).
- The editor shows sample text for elements that currently have nothing to show.
- On release the nearest of the nine screen anchors is chosen and the offset recalculated, so an element stays at the
  same screen edge for every window size and GUI scale. Esc / *Done* saves into `perilscope-client.toml`, *Reset*
  restores the defaults.

## Project layout

| Package | Content |
|---|---|
| `core` | Loader independent logic: `DifficultyState`, payload, `DifficultyTracker`, source interfaces |
| `compat` | Calls into Dynamic Difficulty / Dungeon Difficulty (only instantiated if the mod is loaded) |
| `client`, `client.hud`, `client.screen` | Client state, HUD elements, anchor / position calculation, rendering, layout editor screen |
| `neoforge`, `neoforge.client` | NeoForge entry points, events, payload registration, `ModConfigSpec`, config menu, key binding |

## Development

Requires Java 21.

- Dynamic Difficulty `1.3.4+1.21.1` from `https://maven.muon.rip/releases/` (it needs Fzzy Config and Kotlin for Forge
  at runtime, the build pulls them in for the dev runs).
- Dungeon Difficulty `3.9.1+1.21.1` from the Modrinth Maven (`maven.modrinth:dungeon-difficulty`). If it cannot be
  resolved, download `dungeon_difficulty-neoforge-3.9.1+1.21.1.jar` into `libs/`; the build then uses the local jar
  instead (path configurable via `dungeon_difficulty_local_jar` in `gradle.properties`).

```
./gradlew build        # jar in build/libs
./gradlew runClient    # dev client, Dynamic Difficulty and Dungeon Difficulty are included via localRuntime
./gradlew runServer    # dev dedicated server (accept the EULA in run/eula.txt on first start)
```

### Testing

1. `runClient`, create a world: "Area Level N" is shown top left (colored by level). Walk around / enter a structure
   to see the bonuses change. Enter a Dungeon Difficulty zone and its type and level appear below; leave it and the
   element disappears. Settlements and ruins are shown in green, the other zones from yellow to red.
2. Press F1 or F3: the HUD elements are hidden.
3. Open the layout editor (*Mods → Perilscope → Config → Layout & Style*): drag, scale, hide and nudge both elements,
   cycle the frame styles with the buttons or `F`, toggle the background with `B`. Close with Esc and check that the
   layout is saved in `config/perilscope-client.toml` and kept after a restart.
4. Resize the window and change the GUI scale: the elements stay on screen at the same screen edge.
5. *Reset* in the editor restores the default positions; with `PANEL` the two default positions overlap, drag them apart.
6. `runServer`: the server starts without client class errors. Connect with `runClient` (multiplayer, `localhost`)
   and check that the values are shown. A client without Perilscope can join the same server without errors (no HUD).
7. Remove a dependency from `localRuntime` in `build.gradle`: the game still starts, the corresponding element stays
   hidden.

## License and credits

MIT, see [LICENSE](LICENSE). `TEMPLATE_LICENSE.txt` covers the files taken from the
[NeoForged MDK](https://github.com/NeoForged/MDK) template.

- [Dynamic Difficulty](https://github.com/muon-rw/Dynamic-Difficulty) by MuonR and
  [Dungeon Difficulty](https://modrinth.com/mod/dungeon-difficulty) by ZsoltMolnarrr provide the values and zones
  that are shown. Perilscope is an unofficial companion and not affiliated with either mod.
- Bug reports and suggestions: [issue tracker](https://github.com/Drehverschluss/perilscope/issues).
