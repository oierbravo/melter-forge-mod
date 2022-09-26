# Melter

A simple melter block that turn blocks into liquid.

Intended for modpacks. Use as you see fit.

Not providing any recipe at the moment.

Designed to work with create but it's optional.

High tier heat sources are from Create.

## Heat sources.
- Torch
- Campfire
- Lava
- Blaze burner

## Recipe example:
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
  "processingTime": 500
}
```

## KubeJS Integration
```
//.melterMelting(OUTPUT_FLUID,INPUT_BLOCK).processingTime(INT);
event.recipes.melterMelting(Fluid.of('minecraft:water', 200),"#minecraft:leaves").processingTime(200); //Water generator
event.recipes.melterMelting(Fluid.of('minecraft:lava', 250),"#forge:cobblestone").processingTime(1000); //Lava generator
```

