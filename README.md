# Melter

A simple melter block that turn blocks into liquid.

Intended for modpacks. Use as you see fit.

Not providing any recipe at the moment.

Designed to work with create but it's optional.

High tier heat sources are from Create.

## Heat sources.
- None (0)
- Torch (1)
- Campfire (2)
- Lava (4)
- Blaze burner Inactive (8)
- Blaze burner Active (10)
- Blaze burner SuperHeated (16)

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

### with Minimum Heat
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
  "minimumHeat": 8
}
```

## KubeJS Integration
```
//.melterMelting(OUTPUT_FLUID,INPUT_BLOCK).processingTime(INT);
//.minimumHeat(INT) OPTIONAL
event.recipes.melterMelting(Fluid.of('minecraft:water', 200),"#minecraft:leaves").processingTime(200); //Water generator
event.recipes.melterMelting(Fluid.of('minecraft:lava', 250),"#forge:cobblestone").processingTime(1000); //Lava generator
event.recipes.melterMelting(Fluid.of('minecraft:lava', 250),"#forge:cobblestone").processingTime(1000).minimumHeat(8); //Lava generator
```

## CraftTweaker Integration (1.19.2, 1.20.1)
```

//addRecipe(String name, FluidStack outputFluid,Item inputItem, int processingTime, int minimumHeat)

<recipetype:melter:melting>.addRecipe("test_recipe",<fluid:minecraft:water> * 500,<item:minecraft:gravel>, 1000,8);
<recipetype:melter:melting>.addRecipe("test_recipe_2",<fluid:minecraft:lava> * 500,<item:minecraft:sand>, 200,2);

```