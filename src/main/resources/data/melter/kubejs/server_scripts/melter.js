ServerEvents.recipes(event => {
	//event.recipes.melterMelting([Fluid.of('minecraft:lava',250)],['minecraft:cobblestone']).processingTime(1000);
	// event.recipes.melterMelting([Fluid.of('minecraft:water', 500)],'minecraft:ice').processingTime(1000);
	//event.recipes.melterMelting([Fluid.of('minecraft:water', 250)],'minecraft:snow_block').processingTime(1000);
	//event.recipes.melterMelting([Fluid.of('productivebees:honey', 1000)],'minecraft:honey_block').processingTime(1000);



    //    event.custom({
    //	  type: 'melter:melting',
    //      input: {
    //        item: 'minecraft:cobblestone',
    //        count: 1
    //      },
    //      output: [Fluid.of('minecraft:lava', 250)],
    //      processingTime:1000
    //	});

 })

//ServerEvents.highPriorityData(event => {
 //   event.addJson('melter:data/recipes/melting/honey_block_melting.json', {
//	  type: 'melter:melting',
 //     input: {
 //       item: 'minecraft:honey_block',
 //       count: 1
 //     },
 //     output: {
//
  //      amount: 1000
  //    },
  //    processingTime: 2000,
   //   conditions: [
  //      {
  //        type: 'forge:mod_loaded',
  //        modid: 'productivebees'
   //     }
 //     ]
//	});

    //event.addJson('melter:data/recipes/melting/cobble_melting.json', {
	//  type: 'melter:melting',
    //  input: {
    //    item: 'minecraft:cobblestone',
    //    count: 1
    //  },
    //  output: {
    //    fluid: 'minecraft:lava',
    //    amount: 250
    //  },
    //  processingTime: 1000
	//});



 // })