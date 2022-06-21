package net.flytre.hplus.mixin;


import net.flytre.flytre_lib.api.storage.upgrade.UpgradeInventory;
import net.flytre.hplus.recipe.NbtHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(MinecartItem.class)
public class MinecartItemMixin extends Item {

    private MinecartItemMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void hplus$copyUpgradesToDispensedMinecart(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir, World world, BlockPos blockPos, BlockState blockState, ItemStack stack, RailShape railShape, double yOffset, AbstractMinecartEntity minecartEntity) {
        @Nullable NbtCompound nbt = stack.getSubNbt("EntityTag");
        if (nbt != null && nbt.contains("Upgrades"))
            UpgradeInventory.fromTag(nbt, ((UpgradeInventory) minecartEntity).getUpgrades());
    }



    private void hplus$customTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        DefaultedList<ItemStack> items = NbtHelper.getUpgrades(stack, "EntityTag");

        if (items == null) {
            return;
        }

        Style style = Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.GRAY)).withItalic(true);
        for (ItemStack i : items) {
            if (!i.isEmpty())
                tooltip.add((Text.translatable(i.getTranslationKey())).setStyle(style));
        }
    }
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if(this == Items.HOPPER_MINECART) {
            hplus$customTooltip(stack, world, tooltip, context);
        }
        super.appendTooltip(stack, world, tooltip, context);
    }

}
