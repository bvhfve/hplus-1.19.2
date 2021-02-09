package net.flytre.hplus2.filter;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.flytre_lib.common.inventory.FilterInventory;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class FilterUpgrade extends ItemUpgrade implements HopperUpgrade {


    public FilterUpgrade(Settings settings) {
        super(settings);
    }

    public static FilterInventory getInventory(ItemStack stack) {
        CompoundTag filter = stack.getOrCreateSubTag("filter");
        return FilterInventory.fromTag(filter, 3);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.openHandledScreen(new NamedScreenHandlerFactory() {
            @Override
            public Text getDisplayName() {
                return new TranslatableText("container.hplus.upgrade_filter");
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                FilterInventory filter = getInventory(player.getMainHandStack());
                return new FilterScreenHandler(syncId, inv, filter);
            }
        });
        return super.use(world, user, hand);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        Set<Item> filter = getInventory(stack).getFilterItems();
        if (filter == null)
            return;
        int len = 0;
        Style style = Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.GRAY)).withItalic(true);
        for (Item i : filter) {
            tooltip.add((new TranslatableText(i.getTranslationKey())).setStyle(style));
            if (++len > 8)
                break;
        }
        if (len >= 9) {
            tooltip.add((new LiteralText("§7§o"))
                    .append(new TranslatableText("item.hplus.filter_upgrade.tooltip", filter.size() - 9).setStyle(style)));
        }

        Style mode_text = Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.WHITE));
        Style red_text = Style.EMPTY.withColor(Formatting.RED);
        Style green_text = Style.EMPTY.withColor(Formatting.GREEN);
        MutableText whitelist = new TranslatableText("item.hplus.filter_upgrade.whitelist").setStyle(green_text);
        MutableText blacklist = new TranslatableText("item.hplus.filter_upgrade.blacklist").setStyle(red_text);
        int mode = getInventory(stack).getFilterType();
        tooltip.add(new TranslatableText("item.hplus.filter_upgrade.mode").setStyle(mode_text).append(
                mode == 0 ? whitelist : blacklist
        ));

    }


}
