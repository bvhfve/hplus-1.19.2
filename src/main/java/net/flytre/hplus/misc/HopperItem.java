package net.flytre.hplus.misc;

import net.flytre.flytre_lib.api.base.util.InventoryUtils;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HopperItem extends BlockItem {
    public HopperItem(Block block, Settings settings) {
        super(block, settings);
    }

    public static ItemStack getWithUpgradeData(DefaultedList<ItemStack> inv) {
        NbtCompound upgrades = new NbtCompound();
        InventoryUtils.writeNbt(upgrades, inv, "Upgrades");
        NbtCompound base = new NbtCompound();
        base.put("BlockEntityTag", upgrades);
        ItemStack result = new ItemStack(Items.HOPPER);
        result.setNbt(base);
        return result;
    }

    public static DefaultedList<ItemStack> getUpgrades(ItemStack stack) {
        NbtCompound tag = stack.getSubNbt("BlockEntityTag");

        if (tag == null)
            return null;

        DefaultedList<ItemStack> result = DefaultedList.ofSize(4, ItemStack.EMPTY);
        InventoryUtils.readNbt(tag, result, "Upgrades");

        return result;
    }


    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        DefaultedList<ItemStack> items = getUpgrades(stack);

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
