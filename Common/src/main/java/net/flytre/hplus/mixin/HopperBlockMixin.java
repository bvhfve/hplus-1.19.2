package net.flytre.hplus.mixin;

import net.flytre.flytre_lib.api.storage.upgrade.UpgradeInventory;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.flytre.hplus.misc.StaticConstants.FROM;
import static net.flytre.hplus.misc.StaticConstants.HOPPER_SHAPES;
import static net.flytre.hplus.misc.StaticConstants.HOPPER_SHAPES_RAYCAST;

/**
 * BlockState and model for upward hoppers
 */
@Mixin(value = HopperBlock.class, priority = 99)
public abstract class HopperBlockMixin extends BlockWithEntity {


    @Mutable
    @Shadow
    @Final
    public static DirectionProperty FACING;
    @Shadow
    @Final
    public static BooleanProperty ENABLED;

    static {

        FACING = Properties.FACING;
    }

    protected HopperBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void hplus$setDefaultState(Settings settings, CallbackInfo ci) {
        this.setDefaultState(this.stateManager.getDefaultState().with(FROM, Direction.UP));
    }

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void hplus$getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        cir.setReturnValue(HOPPER_SHAPES[state.get(FACING).ordinal()][state.get(FROM).ordinal()]);
    }

    @Inject(method = "getRaycastShape", at = @At("HEAD"), cancellable = true)
    private void hplus$getRaycastShape(BlockState state, BlockView world, BlockPos pos, CallbackInfoReturnable<VoxelShape> cir) {
        cir.setReturnValue(HOPPER_SHAPES_RAYCAST[state.get(FACING).ordinal()][state.get(FROM).ordinal()]);
    }

    @Inject(method = "getPlacementState", at = @At("HEAD"), cancellable = true)
    private void hplus$getPlacementState(ItemPlacementContext ctx, CallbackInfoReturnable<BlockState> cir) {
        Direction direction = ctx.getSide().getOpposite();
        cir.setReturnValue(this.getDefaultState()
                .with(FACING, direction)
                .with(ENABLED, true)
                .with(FROM, ctx.getPlayerLookDirection().getOpposite())
        );
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockentity = world.getBlockEntity(pos);
        if (blockentity instanceof HopperBlockEntity hopperEntity) {
            if (!world.isClient && player.isCreative() && !((UpgradeInventory) hopperEntity).hasNoUpgrades()) {
                ItemStack itemstack = new ItemStack(Items.HOPPER);
                blockentity.setStackNbt(itemstack);
                itemstack.getOrCreateSubNbt("BlockEntityTag").remove("Inventory");
                if (hopperEntity.hasCustomName()) {
                    itemstack.setCustomName(hopperEntity.getCustomName());
                }
                ItemEntity itementity = new ItemEntity(world, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, itemstack);
                itementity.setToDefaultPickupDelay();
                world.spawnEntity(itementity);

                return;
            }
        }
        super.onBreak(world, pos, state, player);
    }

    @Inject(method = "appendProperties", at = @At("TAIL"))
    private void hplus$addFromProperty(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(FROM);
    }

    @Inject(method = "rotate", at = @At("HEAD"), cancellable = true)
    private void hplus$rotate(BlockState state, BlockRotation rotation, CallbackInfoReturnable<BlockState> cir) {
        cir.setReturnValue(
                state.with(FACING, rotation.rotate(state.get(FACING)))
                        .with(FROM, rotation.rotate(state.get(FROM)))
        );
    }
}
