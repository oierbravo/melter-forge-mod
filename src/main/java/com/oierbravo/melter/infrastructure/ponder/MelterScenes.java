package com.oierbravo.melter.infrastructure.ponder;

import com.oierbravo.melter.content.melter.MelterBlock;
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
        builder.title("melter", "Processing Items in the Melter");
        builder.configureBasePlate(0, 0, 5);
        builder.showBasePlate();

        BlockPos melter = util.grid().at(2, 2, 2);
        builder.world().modifyBlock(melter,blockState -> blockState.setValue(MelterBlock.HEAT_SOURCE,0), false);
        Selection melterSelect = util.select().position(2, 2, 2);

        builder.idle(5);
        builder.world().showSection(melterSelect, Direction.DOWN);
        builder.idle(10);

        Vec3 melterTop = util.vector().topOf(melter);
        builder.overlay().showText(60)
                .attachKeyFrame()
                .text("Transforms blocks into fluids")
                .pointAt(melterTop)
                .placeNearTarget();
        builder.idle(70);

        Selection heatSelect = util.select().position(2, 1, 2);
        builder.world().showSection(heatSelect, Direction.WEST);
        builder.world().modifyBlock(melter,blockState -> blockState.setValue(MelterBlock.HEAT_SOURCE,3), false);

        builder.effects().indicateSuccess(melter);
        builder.idle(10);

        builder.overlay().showText(60)
                .attachKeyFrame()
                .colored(PonderPalette.GREEN)
                .text("They can be heated from the below using different heat sources")
                .pointAt(util.vector().centerOf(melter.below(1)))
                .placeNearTarget();
        builder.idle(70);

        ItemStack itemStack = new ItemStack(Items.COBBLESTONE,32);
        Vec3 entitySpawn = util.vector().topOf(melter.above(3));

        ElementLink<EntityElement> entity1 =
                builder.world().createItemEntity(entitySpawn, util.vector().of(0, 0.2, 0), itemStack);
        builder.idle(18);
        builder.world().modifyEntity(entity1, Entity::discard);
        builder.world().modifyBlockEntity(melter, MelterBlockEntity.class,
                ms -> ms.getItemHandler().insertItem(0, itemStack, true));
        builder.idle(10);
        builder.overlay().showControls(util.vector().blockSurface(melter, Direction.NORTH), Pointing.RIGHT, 30).withItem(itemStack);
        builder.idle(7);

        builder.overlay().showText(40)
                .attachKeyFrame()
                .text("Throw items at the top or insert from the sides")
                .pointAt(melterTop)
                .placeNearTarget();
        builder.idle(60);

        builder.overlay().showText(50)
                .text("Outputs can be extracted by automation")
                .pointAt(util.vector().centerOf(melter))
                .placeNearTarget();
        builder.idle(60);
    }
}
