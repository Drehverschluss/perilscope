Perilscope
==========

NeoForge mod for Minecraft 1.21.1 (Java 21) that shows two freely positionable HUD elements:

1. **Area Difficulty** – area level of [Dynamic Difficulty](https://maven.muon.rip/releases/) at the player
   position, optionally with structure and biome bonus.
2. **Dungeon Difficulty** – type and level of the [Dungeon Difficulty](https://modrinth.com/mod/dungeon-difficulty)
   zone. Only visible while the player stands inside a zone.

All values are computed on the server (every 10 ticks per player) and sent to the client with the
`perilscope:difficulty_state` payload, only when they changed (plus on login and dimension change).
The mod is required on server **and** client. Both integrations are optional.

Project layout
--------------

| Package | Content |
|---|---|
| `core` | Loader independent logic: `DifficultyState`, payload, `DifficultyTracker`, source interfaces |
| `compat` | Calls into Dynamic Difficulty / Dungeon Difficulty (only instantiated if the mod is loaded) |
| `client`, `client.hud` | Client state, HUD elements, anchor / position calculation, rendering |
| `neoforge`, `neoforge.client` | NeoForge entry points, events, payload registration, `ModConfigSpec` |

Dungeon Difficulty has no public API. Perilscope uses its internal classes
(`net.dungeon_difficulty.logic.PatternMatching`, `Difficulty`), so an update of Dungeon Difficulty may break
the integration. On any error the query is logged once and disabled for the rest of the server session.

Configuration
-------------

Client config `config/perilscope-client.toml` (also editable in-game via *Mods → Perilscope → Config*),
per element: `anchor` (`TOP_LEFT`, `TOP_CENTER`, `TOP_RIGHT`, `CENTER_LEFT`, `CENTER_RIGHT`, `BOTTOM_LEFT`,
`BOTTOM_CENTER`, `BOTTOM_RIGHT`), `offsetX`, `offsetY` (GUI pixels, added to the anchor point), `scale`, `visible`.
The area element additionally has `showBonuses`.

Building and testing
--------------------

```
./gradlew build        # jar in build/libs
./gradlew runClient    # dev client, Dynamic Difficulty and Dungeon Difficulty are included via localRuntime
./gradlew runServer    # dev dedicated server (accept the EULA in run/eula.txt on first start)
```

Test checklist:

* `runClient`, create a world: "Area Level N" is shown top left. Walk around / enter a structure to see the
  bonuses change. Enter a Dungeon Difficulty zone (e.g. a structure configured in Dungeon Difficulty's config)
  and the zone line appears directly below; leave it and the line disappears.
* Press F1 or F3: the HUD elements are hidden.
* Change anchor / offsets / scale in the config screen: the elements move immediately and stay on screen at
  every window size and GUI scale.
* `runServer`: the server starts without client class errors. Connect with `runClient` (multiplayer,
  `localhost`) and check that the values are shown.
* Remove a dependency from `localRuntime` in `build.gradle`: the game still starts, the corresponding element
  stays hidden.
