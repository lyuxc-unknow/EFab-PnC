# EFab PNC Compat

EFab PNC Compat adds PneumaticCraft: Repressurized support to EFab recipes.

The mod provides a Pneumatic Interface block and two EFab recipe requirements:

- `efabpnc:pneumatic_air`
- `efabpnc:pneumatic_heat`

Recipes can require compressed air, minimum pressure, heat, or minimum temperature. The same requirements are exposed to CraftTweaker and KubeJS.

## Requirements

- Minecraft `1.21.1`
- NeoForge `21.1.x`
- EFab
- PneumaticCraft: Repressurized

CraftTweaker and KubeJS integrations are available when those mods are installed.

## Pneumatic Interface

The Pneumatic Interface is the EFab-side bridge to PneumaticCraft networks.

Place the Pneumatic Interface inside the EFab crafting area so EFab can find it while checking recipe requirements. The block exposes PneumaticCraft air and heat capabilities on all sides.

Internal values:

- Air volume: `25000`
- Heat capacity: `500`
- Heat resistance: `10`

## Unit Conversion

### Air and Pressure

`pneumaticAir(air)` consumes PneumaticCraft air units, not bar.

For this mod's Pneumatic Interface:

```text
pressure in bar = stored air / 25000
1 bar = 25000 air
```

Common values:

| Pressure | Air |
| --- | ---: |
| `0.5 bar` | `12500` |
| `1 bar` | `25000` |
| `2 bar` | `50000` |
| `4 bar` | `100000` |

`minPressure` is already in game pressure units, so pass bar directly:

```text
minPressure = 1.0 means at least 1 bar
```

When both `air` and `minPressure` are used, the recipe consumes air only above the reserved pressure.

Example:

```text
pneumaticAir(25000, 1.0)
```

This requires at least `1 bar` and consumes up to `25000 air` without draining below the `1 bar` reserve.

### Heat and Temperature

`pneumaticHeat(heat)` consumes PneumaticCraft heat energy, not Celsius.

For this mod's Pneumatic Interface:

```text
temperature change in K = heat / 500
1 K = 500 heat
1 C temperature difference = 500 heat
```

Common values:

| Temperature difference | Heat |
| --- | ---: |
| `1 K` / `1 C` | `500` |
| `10 K` / `10 C` | `5000` |
| `100 K` / `100 C` | `50000` |

`minTemperature` is passed in Kelvin.

```text
Kelvin = Celsius + 273
```

Common minimum temperatures:

| Celsius | Kelvin value to pass |
| --- | ---: |
| `25 C` | `298` |
| `100 C` | `373` |
| `200 C` | `473` |

When both `heat` and `minTemperature` are used, the recipe consumes heat only above the reserved temperature.

## CraftTweaker

The mod expands EFab's `mods.efab.GridRecipeBuilder`. Add the pneumatic requirement by chaining these methods on an EFab grid recipe builder.

Available methods:

```zenscript
pneumaticAir(air as int)
pneumaticAir(air as int, minPressure as float)
pneumaticAir(phase as string, air as int, minPressure as float, consume as bool)

pneumaticPressure(minPressure as float)

pneumaticHeat(heat as double)
pneumaticHeat(heat as double, minTemperature as double)
pneumaticHeat(phase as string, heat as double, minTemperature as double, consume as bool)

pneumaticTemperature(minTemperature as double)
```

Examples:

```zenscript
// Consume 25000 air, equivalent to 1 bar worth of air in one Pneumatic Interface.
builder.pneumaticAir(25000);

// Require at least 1 bar and consume 25000 air without draining below 1 bar.
builder.pneumaticAir(25000, 1.0);

// Only check pressure, do not consume air.
builder.pneumaticPressure(2.0);

// Consume 5000 heat, equivalent to a 10 K temperature drop on one Pneumatic Interface.
builder.pneumaticHeat(5000);

// Require at least 100 C, passed as 373 K, and consume 5000 heat above that reserve.
builder.pneumaticHeat(5000, 373);

// Only check temperature, do not consume heat.
builder.pneumaticTemperature(373);
```

Notes:

- `air` and `heat` are multiplied by the crafted output amount.
- `consume = false` checks the requirement without draining the resource.
- The current implementation runs these requirements in EFab's `tick` phase. The overloads accept a `phase` string for compatibility, but currently still register the requirement as `tick`.

## KubeJS

The mod adds the same methods to EFab's KubeJS grid recipe builders.

Available methods:

```js
pneumaticAir(air)
pneumaticAir(air, minPressure)
pneumaticAir(air, minPressure, consume)

pneumaticPressure(minPressure)

pneumaticHeat(heat)
pneumaticHeat(heat, minTemperature)
pneumaticHeat(heat, minTemperature, consume)

pneumaticTemperature(minTemperature)
```

Examples:

```js
// Consume 25000 air, equivalent to 1 bar worth of air in one Pneumatic Interface.
builder.pneumaticAir(25000)

// Require at least 1 bar and consume 25000 air without draining below 1 bar.
builder.pneumaticAir(25000, 1.0)

// Only check pressure, do not consume air.
builder.pneumaticPressure(2.0)

// Consume 5000 heat, equivalent to a 10 K temperature drop on one Pneumatic Interface.
builder.pneumaticHeat(5000)

// Require at least 100 C, passed as 373 K, and consume 5000 heat above that reserve.
builder.pneumaticHeat(5000, 373)

// Only check temperature, do not consume heat.
builder.pneumaticTemperature(373)
```

Notes:

- `air` and `heat` are multiplied by the crafted output amount.
- `consume = false` checks the requirement without draining the resource.

## Datapack Requirement JSON

The requirements are also registered for EFab recipe JSON.

Air requirement:

```json
{
  "type": "efabpnc:pneumatic_air",
  "phase": "tick",
  "air": 25000,
  "min_pressure": 1.0,
  "consume": true
}
```

Heat requirement:

```json
{
  "type": "efabpnc:pneumatic_heat",
  "phase": "tick",
  "heat": 5000.0,
  "min_temperature": 373.0,
  "consume": true
}
```

Fields:

| Field | Type | Default | Meaning |
| --- | --- | --- | --- |
| `phase` | string | `tick` | EFab requirement phase |
| `air` | int | `0` | Air units to consume |
| `min_pressure` | float | `0.0` | Minimum pressure in bar |
| `heat` | double | `0.0` | Heat to consume |
| `min_temperature` | double | `0.0` | Minimum temperature in Kelvin |
| `consume` | boolean | `true` | Whether to drain the resource |

## Development

Build with:

```shell
./gradlew build
```

On Windows:

```powershell
.\gradlew.bat build
```
