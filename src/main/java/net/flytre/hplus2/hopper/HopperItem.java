package net.flytre.hplus2.hopper;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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

    public static ItemStack getItemStack(DefaultedList<ItemStack> inv) {
        CompoundTag tag = new CompoundTag();
        Inventories.toTag(tag, inv);
        CompoundTag tag2 = new CompoundTag();
        tag2.put("Upgrades", tag.getList("Items", 10));
        CompoundTag tag3 = new CompoundTag();
        tag3.put("BlockEntityTag", tag2);
        ItemStack result = new ItemStack(Items.HOPPER);
        result.setTag(tag3);
        return result;
    }

    public static DefaultedList<ItemStack> getInventory(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return null;
        }
        tag = tag.getCompound("BlockEntityTag");
        if (tag == null) {
            return null;
        }
        Tag upgrades = tag.get("Upgrades");
        CompoundTag two = new CompoundTag();
        two.put("Items", upgrades);
        DefaultedList<ItemStack> items = DefaultedList.ofSize(9, ItemStack.EMPTY);
        Inventories.fromTag(two, items);
        return items;
    }


    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        DefaultedList<ItemStack> items = getInventory(stack);

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
