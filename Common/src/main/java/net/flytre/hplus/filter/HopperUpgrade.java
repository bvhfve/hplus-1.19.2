package net.flytre.hplus.filter;

import net.flytre.flytre_lib.api.storage.upgrade.UpgradeItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HopperUpgrade extends Item implements UpgradeItem {

    public static final Set<Item> UPGRADES = new HashSet<>();


    public HopperUpgrade() {
        super(new Settings().group(ItemGroup.REDSTONE));
        UPGRADES.add(this);
    }

    public HopperUpgrade(Settings settings) {
        super(settings);
        UPGRADES.add(this);
    }


    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        tooltip.add(new TranslatableText(getTranslationKey() + ".tooltip"));
    }

    @Override
    public Item get() {
        return this;
    }
}
