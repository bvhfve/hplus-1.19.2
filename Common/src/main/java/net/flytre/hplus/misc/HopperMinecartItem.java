package net.flytre.hplus.misc;

import net.flytre.hplus.recipe.NbtHelper;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MinecartItem;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HopperMinecartItem extends MinecartItem {
    public HopperMinecartItem(AbstractMinecartEntity.Type type, Settings settings) {
        super(type, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        DefaultedList<ItemStack> items = NbtHelper.getUpgrades(stack, "EntityTag");

        if (items == null) {
            super.appendTooltip(stack, world, tooltip, context);
            return;
        }

        Style style = Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.GRAY)).withItalic(true);
        for (ItemStack i : items) {
            if (!i.isEmpty())
                tooltip.add((new TranslatableText(i.getTranslationKey())).setStyle(style));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}
