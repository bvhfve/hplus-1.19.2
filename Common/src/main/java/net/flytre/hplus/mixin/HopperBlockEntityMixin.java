package net.flytre.hplus.mixin;

import net.flytre.flytre_lib.api.storage.upgrade.UpgradeInventory;
import net.flytre.hplus.Registry;
import net.flytre.hplus.filter.HopperUpgrade;
import net.flytre.hplus.misc.StaticConstants;
import net.flytre.hplus.misc.MixinHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;


/**
 * Makes hoppers implement UpgradeInventory
 * Makes hopper entity logic account for upward hoppers
 */
@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin extends LootableContainerBlockEntity implements Hopper, UpgradeInventory {

    @Unique
    private DefaultedList<ItemStack> upgrades;


    protected HopperBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Shadow
    @Nullable
    private static Inventory getInventoryAt(World world, double x, double y, double z) {
        throw new AssertionError();
    }

    @ModifyVariable(method = "extract(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/HopperBlockEntity;isInventoryEmpty(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/util/math/Direction;)Z"))
    private static Direction hplus$extractDirection(Direction direction, World world, Hopper hopper) {
        return MixinHelper.getExtractDirection(hopper).getOpposite();
    }

    @Inject(method = "getInputInventory", at = @At("HEAD"), cancellable = true)
    private static void hplus$getInputInventory(World world, Hopper hopper, CallbackInfoReturnable<Inventory> cir) {
        Vec3i pos = new Vec3i(hopper.getHopperX(), hopper.getHopperY(), hopper.getHopperZ())
                .add(
                        MixinHelper.getExtractDirection(hopper).getVector()
                );
        cir.setReturnValue(getInventoryAt(world, pos.getX(), pos.getY(), pos.getZ()));
    }



    @Redirect(method = "getInputItemEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/Hopper;getInputAreaShape()Lnet/minecraft/util/shape/VoxelShape;"))
    private static VoxelShape hplus$getSuctionHitbox(Hopper instance) {
        Direction extractDirection = MixinHelper.getExtractDirection(instance);

        int scale = 0;
        if (instance instanceof UpgradeInventory upgradeInventory) {
            if (upgradeInventory.hasUpgrade(Registry.SUCTION_UPGRADE.get()))
                scale = 1;
            if (upgradeInventory.hasUpgrade(Registry.BLACK_HOLE_UPGRADE.get()))
                scale = 2;
        }

        return StaticConstants.SUCTION_AREA[extractDirection.ordinal()][scale];
    }

    @Override
    public DefaultedList<ItemStack> getUpgrades() {
        return upgrades;
    }

    @Inject(method = "<init>*", at = @At("TAIL"))
    private void hplus$init(BlockPos pos, BlockState state, CallbackInfo ci) {
        upgrades = DefaultedList.ofSize(5, ItemStack.EMPTY);
        markUpgradesDirty();
    }

    @Override
    public Set<Item> validUpgrades() {
        return HopperUpgrade.UPGRADES;
    }

    @Inject(method = "writeNbt", at = @At("RETURN"))
    private void hplus$writeNbt(NbtCompound nbt, CallbackInfo ci) {
        UpgradeInventory.toTag(nbt, upgrades);
    }

    @Inject(method = "readNbt", at = @At("RETURN"))
    private void hplus$readNbt(NbtCompound nbt, CallbackInfo ci) {
        UpgradeInventory.fromTag(nbt, upgrades);
        markUpgradesDirty();
    }

    @Override
    public boolean isValidUpgrade(ItemStack stack) {
        return validUpgrades().contains(stack.getItem()) && !hasUpgrade(stack.getItem());
    }

    /**
     * O(1) has upgrade
     */
    @Override
    public boolean hasUpgrade(Item upgrade) {
        return upgrades
                .stream()
                .map(ItemStack::getItem)
                .anyMatch(i -> i.equals(upgrade));
    }

    @Override
    public void markUpgradesDirty() {
        if (this.world != null) {
            markDirty(this.world, this.pos, this.getCachedState());
        }
    }
}
