package net.flytre.hplus.filter;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.flytre_lib.api.storage.inventory.filter.FilterInventory;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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

public class FilterUpgrade extends HopperUpgrade {


    public FilterUpgrade(Settings settings) {
        super(settings);
    }

    public static FilterInventory getInventory(ItemStack stack) {
        NbtCompound filter = stack.getOrCreateSubNbt("filter");
        return FilterInventory.readNbt(filter, 3);
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
                ItemStack stack = player.getOffHandStack();
                if (!(stack.getItem() instanceof FilterUpgrade))
                    stack = player.getMainHandStack();
                FilterInventory filter = getInventory(stack);
                return new FilterScreenHandler(syncId, inv, filter);
            }
        });
        return super.use(world, user, hand);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        FilterInventory inv = getInventory(stack);
        Set<Item> filter = inv.getFilterItems();
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

        Style red_text = Style.EMPTY.withColor(Formatting.RED);
        Style green_text = Style.EMPTY.withColor(Formatting.GREEN);
        MutableText whitelist = new TranslatableText("item.hplus.filter_upgrade.whitelist").setStyle(green_text);
        MutableText blacklist = new TranslatableText("item.hplus.filter_upgrade.blacklist").setStyle(red_text);

        MutableText matchMod = new TranslatableText("item.hplus.filter_upgrade.mod_match.true").setStyle(green_text);
        MutableText ignoreMod = new TranslatableText("item.hplus.filter_upgrade.mod_match.false").setStyle(red_text);

        MutableText matchNbt = new TranslatableText("item.hplus.filter_upgrade.nbt_match.true").setStyle(green_text);
        MutableText ignoreNbt = new TranslatableText("item.hplus.filter_upgrade.nbt_match.false").setStyle(red_text);


        tooltip.add(new LiteralText("").append(inv.getFilterType() == 0 ? whitelist : blacklist));
        tooltip.add(new LiteralText("").append(inv.isMatchMod() ? matchMod : ignoreMod));
        tooltip.add(new LiteralText("").append(inv.isMatchNbt() ? matchNbt : ignoreNbt));
    }


}
