package net.flytre.hplus.mixin;


import net.flytre.flytre_lib.api.storage.upgrade.UpgradeInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin extends Entity {

    public AbstractMinecartEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Redirect(method = "dropItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;dropStack(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/ItemEntity;"))
    public ItemEntity hplus$dropStackedHopper(AbstractMinecartEntity instance, ItemStack stack) {

        if (instance instanceof HopperMinecartEntity) {
            NbtCompound tag = stack.getOrCreateSubNbt("BlockEntityTag");
            UpgradeInventory.toTag(tag, ((UpgradeInventory) instance).getUpgrades());
            return dropStack(stack);
        }
        return dropStack(stack);
    }
}
