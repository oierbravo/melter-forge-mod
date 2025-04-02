package com.oierbravo.melter.infrastructure.ponder;

import com.oierbravo.melter.content.melter.MelterBlockEntity;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class MelterScenes {
    public static void melter(SceneBuilder builder, SceneBuildingUtil util) {
        //CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        //SceneBuilder scene = new PonderSceneBuilder();
        builder.title("melter", "Processing Items in the Sifter");
        builder.configureBasePlate(0, 0, 5);

        Selection belt = util.select().fromTo(1, 1, 5, 0, 1, 2)
                .add(util.select().position(1, 2, 2));
        Selection beltCog = util.select().position(2, 0, 5);

        builder.world().showSection(util.select().layer(0)
                .substract(beltCog), Direction.UP);

        BlockPos sifter = util.grid().at(2, 2, 2);
        Selection sifterSelect = util.select().position(2, 2, 2);
        Selection cogs = util.select().fromTo(3, 1, 2, 3, 2, 2);

        builder.idle(5);
        builder.world().showSection(util.select().position(4, 1, 3), Direction.DOWN);
        builder.world().showSection(util.select().position(2, 1, 2), Direction.DOWN);
        builder.idle(10);
        builder.world().showSection(util.select().position(sifter), Direction.DOWN);
        builder.idle(10);
        Vec3 sifterTop = util.vector().topOf(sifter);
        builder.overlay().showText(60)
                .attachKeyFrame()
                .text("Sifter process items by sifting them")
                .pointAt(sifterTop)
                .placeNearTarget();
        builder.idle(70);

        builder.world().showSection(cogs, Direction.DOWN);
        builder.idle(10);

        builder.effects().indicateSuccess(sifter);
        builder.idle(10);

        builder.overlay().showText(60)
                .attachKeyFrame()
                .colored(PonderPalette.GREEN)
                .text("They can be powered from the side using cogwheels")
                .pointAt(util.vector().topOf(sifter.east()))
                .placeNearTarget();
        builder.idle(70);

        ItemStack itemStack = new ItemStack(Items.GRAVEL,32);
        Vec3 entitySpawn = util.vector().topOf(sifter.above(3));

        ElementLink<EntityElement> entity1 =
                builder.world().createItemEntity(entitySpawn, util.vector().of(0, 0.2, 0), itemStack);
        builder.idle(18);
        builder.world().modifyEntity(entity1, Entity::discard);
        builder.world().modifyBlockEntity(sifter, MelterBlockEntity.class,
                ms -> ms.getItemHandler().insertItem(0, itemStack, true));
        builder.idle(10);
        builder.overlay().showControls(util.vector().blockSurface(sifter, Direction.NORTH), Pointing.RIGHT, 30).withItem(itemStack);
        builder.idle(7);

        builder.overlay().showText(40)
                .attachKeyFrame()
                .text("Throw or Insert items at the top")
                .pointAt(sifterTop)
                .placeNearTarget();
        builder.idle(60);

        /*builder.world().modifyBlockEntity(sifter, MelterBlockEntity.class,
                ms -> ms.getItemHandler()..setStackInSlot(0, ItemStack.EMPTY));*/

        builder.overlay().showText(50)
                .text("After some time, the result can be obtained via Right-click")
                .pointAt(util.vector().blockSurface(sifter, Direction.WEST))
                .placeNearTarget();
        builder.idle(60);

        /*ItemStack nugget = AllItems.COPPER_NUGGET.asStack();
        builder.overlay().showControls(util.vector().blockSurface(sifter, Direction.NORTH), Pointing.RIGHT, 30).rightClick();
        builder.idle(50);

        builder.addKeyframe();
        builder.world().showSection(beltCog, Direction.UP);
        builder.world().showSection(belt, Direction.EAST);
        builder.idle(15);

        BlockPos beltPos = util.grid().at(1, 1, 2);
        builder.world().createItemOnBelt(beltPos, Direction.EAST, nugget);
        builder.idle(15);
        builder.world().createItemOnBelt(beltPos, Direction.EAST, new ItemStack(Items.COAL));
        builder.idle(20);

        builder.overlay().showText(50)
                .text("The outputs can also be extracted by automation")
                .pointAt(util.vector().blockSurface(sifter, Direction.WEST)
                        .add(-.5, .4, 0))
                .placeNearTarget();*/
        builder.idle(60);
    }
}
