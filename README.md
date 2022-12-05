# Melter

A simple melter block that turn blocks into liquid.

Intended for modpacks. Use as you see fit.

Not providing any recipe at the moment.

Designed to work with create but it's optional.

High tier heat sources are from Create.

## Heat sources.
BLOCK - Heat level
- Torch - 1
- Campfire - 2
- Lava - 4
- Blaze burner Inactive 8
- Blaze burner Fading 9
- Blaze burner Active 10
- Blaze burner Super Heated 16

## Recipe example:
- `processingTime` default is `200`
- `minimumHeat` default is `1` (torch)
```
{
  "type": "melter:melting",
  "input": {
    "tag": "forge:cobblestone",
    "count": 1
  },
  "output": {
    "fluid": "minecraft:lava",
    "amount": 250
  },
  "processingTime": 500,
  "minimumHeat": 1
}
```

## KubeJS Integration
```
//.melterMelting(OUTPUT_FLUID,INPUT_BLOCK).processingTime(INT);
event.recipes.melterMelting(Fluid.of('minecraft:water', 200),"#minecraft:leaves").processingTime(200); //Water generator
event.recipes.melterMelting(Fluid.of('minecraft:lava', 250),"#forge:cobblestone").processingTime(1000); //Lava generator
event.recipes.melterMelting(Fluid.of('minecraft:lava', 250),"#forge:cobblestone").processingTime(1000).minimunHeat(2); //Lava generator with campfire requeriment
```

