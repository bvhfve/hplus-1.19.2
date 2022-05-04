package net.flytre.hplus.mixin;


import net.flytre.hplus.recipe.NbtHelper;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BlockItem.class)
public class BlockItemMixin {

    @Inject(method="appendTooltip", at = @At("HEAD"))
    private void hplus$customHopperTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        if((Object)this == Items.HOPPER) {
            hplus$addTooltip(stack, world, tooltip, context);
        }
    }

    private void hplus$addTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        DefaultedList<ItemStack> items = NbtHelper.getUpgrades(stack, "BlockEntityTag");

        if (items == null) {
            return;
        }

        Style style = Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.GRAY)).withItalic(true);
        for (ItemStack i : items) {
            if (!i.isEmpty())
                tooltip.add((new TranslatableText(i.getTranslationKey())).setStyle(style));
        }
    }
}
