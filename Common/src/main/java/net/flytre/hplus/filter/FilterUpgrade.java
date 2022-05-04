package net.flytre.hplus.filter;

import net.flytre.flytre_lib.api.storage.inventory.filter.FilterInventory;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        FilterInventory.appendFilterToTooltip(getInventory(stack), tooltip);
    }


}
