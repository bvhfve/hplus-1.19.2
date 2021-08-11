package net.flytre.hplus.mixin;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.rmi.registry.RegistryHandler;

/**
 * BlockState and model for upward hoppers
 */
@Mixin(value = HopperBlock.class, priority = 99)
public abstract class HopperBlockMixin extends BlockWithEntity {

    @Unique
    private static final VoxelShape UP_RAY_TRACE_SHAPE;
    @Unique
    private static final VoxelShape UP_SHAPE;
    @Unique
    private static final VoxelShape TOP_SHAPE_INVERTED;
    @Unique
    private static final VoxelShape MIDDLE_SHAPE_INVERTED;
    @Unique
    private static final VoxelShape OUTSIDE_SHAPE_INVERTED;
    @Unique
    private static final VoxelShape DEFAULT_SHAPE_INVERTED;
    @Unique
    private static final VoxelShape INSIDE_SHAPE_INVERTED;
    @Mutable
    @Shadow
    @Final
    public static DirectionProperty FACING;
    @Shadow
    @Final
    public static BooleanProperty ENABLED;

    static {

        FACING = Properties.FACING;
        TOP_SHAPE_INVERTED = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D);
        MIDDLE_SHAPE_INVERTED = Block.createCuboidShape(4.0D, 6.0D, 4.0D, 12.0D, 12, 12.0D);
        OUTSIDE_SHAPE_INVERTED = VoxelShapes.union(MIDDLE_SHAPE_INVERTED, TOP_SHAPE_INVERTED);
        INSIDE_SHAPE_INVERTED = Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 5.0D, 14.0D);
        DEFAULT_SHAPE_INVERTED = VoxelShapes.combineAndSimplify(OUTSIDE_SHAPE_INVERTED, INSIDE_SHAPE_INVERTED, BooleanBiFunction.ONLY_FIRST);
        UP_SHAPE = VoxelShapes.union(DEFAULT_SHAPE_INVERTED, Block.createCuboidShape(6.0D, 12.0D, 6.0D, 10.0D, 16.0D, 10.0D));
        UP_RAY_TRACE_SHAPE = Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 5.0D, 14.0D);
    }

    protected HopperBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    public void hplus$getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (state.get(FACING) == Direction.UP)
            cir.setReturnValue(UP_SHAPE);
    }

    @Inject(method = "getRaycastShape", at = @At("HEAD"), cancellable = true)
    public void hplus$getRaycastShape(BlockState state, BlockView world, BlockPos pos, CallbackInfoReturnable<VoxelShape> cir) {
        if (state.get(FACING) == Direction.UP)
            cir.setReturnValue(UP_RAY_TRACE_SHAPE);
    }

    @Inject(method = "getPlacementState", at = @At("HEAD"), cancellable = true)
    public void hplus$getPlacementState(ItemPlacementContext ctx, CallbackInfoReturnable<BlockState> cir) {
        Direction direction = ctx.getSide().getOpposite();
        cir.setReturnValue(this.getDefaultState().with(FACING, direction).with(ENABLED, true));
    }
}
