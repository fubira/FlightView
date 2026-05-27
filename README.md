# FlightView

[![Build](https://github.com/fubira/FlightView/actions/workflows/build.yml/badge.svg)](https://github.com/fubira/FlightView/actions/workflows/build.yml)

Minecraft client mod that displays elytra flight information. Available for both Fabric and NeoForge.

## Features

- **Flight HUD** — ground speed (GS) and air speed (AS) in km/h, plus yaw and pitch
- **Elytra durability** — remaining durability, colored as it wears down
- **Auto camera** — switches to third-person back view while flying, restores the previous view on landing

## Controls

Press **V** to cycle through modes:

| Mode | Behavior |
|------|----------|
| 0 | Disabled |
| 1 | HUD only |
| 2 | HUD + auto camera |

## Tech stack

Java 25, Gradle. Fabric Loom for the Fabric module and NeoForge ModDev for the NeoForge module. Settings persist through Cloth Config (Fabric) and the NeoForge config spec (NeoForge).

## Requirements

Minecraft and mod-loader versions are defined in [`gradle.properties`](gradle.properties).

## Build

```sh
./gradlew build
```

Combined artifacts are written to `build/libs/`.

## Structure

| Module | Contents |
|--------|----------|
| `common` | Shared resources (language files) |
| `fabric` | Fabric implementation |
| `neoforge` | NeoForge implementation |

## License

[MIT](LICENSE.md) © fubira
