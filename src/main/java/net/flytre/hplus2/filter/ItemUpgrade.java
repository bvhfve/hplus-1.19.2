package net.flytre.hplus2.filter;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ItemUpgrade extends Item implements HopperUpgrade {

    public static final ArrayList<ItemUpgrade> UPGRADES = new ArrayList<>();
    private String key;


    public ItemUpgrade() {
        super(new FabricItemSettings().group(ItemGroup.REDSTONE));
        UPGRADES.add(this);
    }

    public ItemUpgrade(Settings settings) {
        super(settings);
        UPGRADES.add(this);
    }

    public ItemUpgrade(String descriptionKey) {
        super(new FabricItemSettings().group(ItemGroup.REDSTONE).maxCount(1));
        this.key = descriptionKey;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        tooltip.add(new TranslatableText(key != null ? key : getTranslationKey() + ".tooltip"));
    }
}
