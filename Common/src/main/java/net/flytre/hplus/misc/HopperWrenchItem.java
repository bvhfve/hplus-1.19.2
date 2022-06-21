package net.flytre.hplus.misc;

import net.flytre.flytre_lib.api.base.compat.wrench.WrenchItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.message.MessageType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

//TODO 1.19: ADD CAN MINE TO WRENCH
public class HopperWrenchItem extends WrenchItem {


    public HopperWrenchItem(Settings settings) {
        super(settings);
    }

    public static void hopperWrenchInteraction(PlayerEntity player, BlockState state, WorldAccess world, BlockPos pos, boolean update, ItemStack stack) {

        if (player == null || world.isClient())
            return;

        if (!(state.getBlock() instanceof HopperBlock))
            return;

        Block block = state.getBlock();
        StateManager<Block, BlockState> stateManager = block.getStateManager();
        Collection<Property<?>> collection = List.of(HopperBlock.FACING, StaticConstants.FROM);
        String string = Registry.BLOCK.getId(block).toString();

        NbtCompound nbtCompound = stack.getOrCreateSubNbt("DebugProperty");
        String string2 = nbtCompound.getString(string);
        Property<?> property = stateManager.getProperty(string2);
        if (update) {
            if (property == null) {
                property = collection.iterator().next();
            }

            BlockState blockState = state;
            do {
                blockState = cycle(blockState, property);
            } while (blockState.get(HopperBlock.FACING) == blockState.get(StaticConstants.FROM));

            world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
            sendMessage(player, Text.translatable(Items.DEBUG_STICK.getTranslationKey() + ".update", property.getName(), getValueString(blockState, property)));
        } else {
            property = cycle(collection, property);
            String blockState = property.getName();
            nbtCompound.putString(string, blockState);
            sendMessage(player, Text.translatable(Items.DEBUG_STICK.getTranslationKey() + ".select", blockState, getValueString(state, property)));
        }
    }

    private static <T extends Comparable<T>> BlockState cycle(BlockState state, Property<T> property) {
        return state.with(property, cycle(property.getValues(), state.get(property)));
    }

    private static <T> T cycle(Iterable<T> elements, @Nullable T current) {
        return Util.next(elements, current);
    }

    private static void sendMessage(PlayerEntity player, Text message) {
        if (player instanceof ServerPlayerEntity)
            player.sendMessage(message, true);
    }

    private static <T extends Comparable<T>> String getValueString(BlockState state, Property<T> property) {
        return property.name(state.get(property));
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        if (!world.isClient) {
            hopperWrenchInteraction(miner, state, world, pos, false, miner.getStackInHand(Hand.MAIN_HAND));
        }
        return false;
    }
}
