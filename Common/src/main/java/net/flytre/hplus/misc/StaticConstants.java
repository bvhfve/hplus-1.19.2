package net.flytre.hplus.misc;

import net.minecraft.block.Block;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class StaticConstants {

    public static final VoxelShape[][] HOPPER_SHAPES;

    public static final VoxelShape[][] HOPPER_SHAPES_RAYCAST;

    public static final VoxelShape[][] SUCTION_AREA;
    public static final DirectionProperty FROM = DirectionProperty.of("from");

    static {

        HOPPER_SHAPES = new VoxelShape[6][6];
        HOPPER_SHAPES_RAYCAST = new VoxelShape[6][6];

        SUCTION_AREA = new VoxelShape[6][3];

        VoxelShape[] fromShapes = new VoxelShape[]{
                Block.createCuboidShape(0, 0, 0, 16, 6, 16),
                Block.createCuboidShape(0, 10, 0, 16, 16, 16),

                Block.createCuboidShape(0, 0, 0, 16, 16, 6),
                Block.createCuboidShape(0, 0, 10, 16, 16, 16),

                Block.createCuboidShape(0, 0, 0, 6, 16, 16),
                Block.createCuboidShape(10, 0, 0, 16, 16, 16),
        };
        VoxelShape[] insideShapes = new VoxelShape[]{
                Block.createCuboidShape(2, 0, 2, 14, 5, 14),
                Block.createCuboidShape(2, 11, 2, 14, 16, 14),

                Block.createCuboidShape(2, 2, 0, 14, 14, 5),
                Block.createCuboidShape(2, 2, 11, 14, 14, 16),

                Block.createCuboidShape(0, 2, 2, 5, 14, 14),
                Block.createCuboidShape(11, 2, 2, 16, 14, 14),
        };

        VoxelShape raycastCore = Block.createCuboidShape(4.0D, 4.0D, 4.0D, 12.0D, 12.0D, 12.0D);
        VoxelShape basicCore = Block.createCuboidShape(6.0D, 6.0D, 6.0D, 10.0D, 10.0D, 10.0D);

        for (var fromDirection : Direction.values()) {
            int fromOrdinal = fromDirection.ordinal();

            SUCTION_AREA[fromOrdinal][0] = VoxelShapes.union(insideShapes[fromOrdinal], createCuboidShape(fromDirection, 0));
            SUCTION_AREA[fromOrdinal][1] = VoxelShapes.union(insideShapes[fromOrdinal], createCuboidShape(fromDirection, 1));
            SUCTION_AREA[fromOrdinal][2] = VoxelShapes.union(insideShapes[fromOrdinal], createCuboidShape(fromDirection, 2));


            VoxelShape mainShapeRaycast = VoxelShapes.union(fromShapes[fromOrdinal], raycastCore);

            VoxelShape mainShape = VoxelShapes.combineAndSimplify(
                    mainShapeRaycast, insideShapes[fromOrdinal], BooleanBiFunction.ONLY_FIRST);

            for (Direction facingDirection : Direction.values()) {

                double pX = facingDirection.getOffsetX() * 0.375;
                double pY = facingDirection.getOffsetY() * 0.375;
                double pZ = facingDirection.getOffsetZ() * 0.375;
                if (!facingDirection.getAxis().equals(fromDirection.getAxis())) {
                    pX += fromDirection.getOffsetX() * -0.125;
                    pY += fromDirection.getOffsetY() * -0.125;
                    pZ += fromDirection.getOffsetZ() * -0.125;
                }
                VoxelShape facingShapeOffset = basicCore.offset(pX, pY, pZ);

                HOPPER_SHAPES[facingDirection.ordinal()][fromOrdinal] = VoxelShapes.union(mainShape, facingShapeOffset);
                HOPPER_SHAPES_RAYCAST[facingDirection.ordinal()][fromOrdinal] = VoxelShapes.union(mainShapeRaycast, facingShapeOffset);
            }
        }
    }

    private static VoxelShape createCuboidShape(Direction fromDirection, int expansions) {
        return Block.createCuboidShape(
                fromDirection.getAxis() == Direction.Axis.X ? 0 : -16 * expansions,
                fromDirection.getAxis() == Direction.Axis.Y ? 0 : -16 * expansions,
                fromDirection.getAxis() == Direction.Axis.Z ? 0 : -16 * expansions,
                fromDirection.getAxis() == Direction.Axis.X ? 16 : 16 + 16 * expansions,
                fromDirection.getAxis() == Direction.Axis.Y ? 16 : 16 + 16 * expansions,
                fromDirection.getAxis() == Direction.Axis.Z ? 16 : 16 + 16 * expansions
        ).offset(fromDirection.getOffsetX(), fromDirection.getOffsetY(), fromDirection.getOffsetZ());
    }
}
