package net.flytre.hplus2.mixin;

import net.flytre.hplus2.hopper.HopperItem;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
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
}
