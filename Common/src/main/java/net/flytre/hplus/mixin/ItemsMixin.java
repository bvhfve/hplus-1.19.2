package net.flytre.hplus.mixin;

import net.flytre.hplus.misc.HopperItem;
import net.flytre.hplus.misc.HopperMinecartItem;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Gives hoppers custom tooltips based on their stored upgrades
 */
@Mixin(Items.class)
public abstract class ItemsMixin {


    @Shadow
    private static Item register(Identifier id, Item item) {
        throw new IllegalStateException();
    }

    @Inject(method = "register(Lnet/minecraft/block/Block;Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;", at = @At("HEAD"), cancellable = true)
    private static void hplus$registerHopperItem(Block block, Item item, CallbackInfoReturnable<Item> cir) {
        if (block == Blocks.HOPPER) {
            cir.setReturnValue(register(Registry.BLOCK.getId(block), new HopperItem(block, new Item.Settings().group(ItemGroup.REDSTONE))));
        }
    }

    @Inject(method = "register(Lnet/minecraft/util/Identifier;Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;", at = @At("HEAD"), cancellable = true)
    private static void hplus$registerMinecartItem(Identifier id, Item item, CallbackInfoReturnable<Item> cir) {
        if (id.getPath().equals("hopper_minecart")) {
            cir.setReturnValue(Registry.register(Registry.ITEM, id, new HopperMinecartItem(AbstractMinecartEntity.Type.HOPPER, (new Item.Settings()).maxCount(1).group(ItemGroup.TRANSPORTATION))));
        }
    }
}
