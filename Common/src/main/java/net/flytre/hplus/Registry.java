package net.flytre.hplus;

import net.flytre.flytre_lib.api.base.compat.wrench.WrenchItem;
import net.flytre.flytre_lib.api.base.compat.wrench.WrenchObservers;
import net.flytre.flytre_lib.api.base.util.PacketUtils;
import net.flytre.flytre_lib.loader.BlockEntityFactory;
import net.flytre.flytre_lib.loader.LoaderAgnosticRegistry;
import net.flytre.hplus.filter.FilterScreenHandler;
import net.flytre.hplus.filter.FilterUpgrade;
import net.flytre.hplus.filter.HopperUpgrade;
import net.flytre.hplus.misc.HopperWrenchItem;
import net.flytre.hplus.misc.StoneHopperEntity;
import net.flytre.hplus.network.FilterC2SPacket;
import net.flytre.hplus.recipe.HopperMinecartRecipe;
import net.flytre.hplus.recipe.HopperUpgradeRecipe;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;

public class Registry {
    public static final Supplier<Item> FILTER_UPGRADE = registerItem(() -> new FilterUpgrade(new Item.Settings().group(ItemGroup.REDSTONE)), "upgrade_filter");
    public static final Supplier<Item> VOID_UPGRADE = registerItem(HopperUpgrade::new, "upgrade_void");
    public static final Supplier<Item> LOCK_UPGRADE = registerItem(HopperUpgrade::new, "upgrade_lock");
    public static final Supplier<Item> BASE_UPGRADE = registerItem(() -> new Item(new Item.Settings().group(ItemGroup.REDSTONE)), "upgrade_base");

    public static Supplier<SpecialRecipeSerializer<HopperUpgradeRecipe>> UPGRADE_RECIPE = LoaderAgnosticRegistry.registerRecipe(() -> new SpecialRecipeSerializer<>(HopperUpgradeRecipe::new), "hplus", "upgrade_recipe");
    public static Supplier<SpecialRecipeSerializer<HopperMinecartRecipe>> HOPPER_MINECART_RECIPE = LoaderAgnosticRegistry.registerRecipe(() -> new SpecialRecipeSerializer<>(HopperMinecartRecipe::new), "hplus", "minecart_recipe");


    public static Supplier<BlockEntityType<StoneHopperEntity>> STONE_HOPPER_ENTITY;

    public static final Supplier<Item> WRENCH = registerItem(() -> new HopperWrenchItem(new Item.Settings().group(ItemGroup.REDSTONE).maxCount(1)), "wrench");



    public static final Supplier<HopperBlock> STONE_HOPPER = registerBlock(() -> new HopperBlock(AbstractBlock.Settings.of(Material.STONE).hardness(3.0f)) {
        @Override
        public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
            return new StoneHopperEntity(pos, state);
        }

        @Nullable
        public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
            return world.isClient ? null : checkType(type, STONE_HOPPER_ENTITY.get(), HopperBlockEntity::serverTick);
        }
    }, "stone_hopper");


    public static <T extends Block> Supplier<T> registerBlock(Supplier<T> block, String id) {
        final var temp = LoaderAgnosticRegistry.registerBlock(block, Constants.MOD_ID, id);
        LoaderAgnosticRegistry.registerItem(() -> new BlockItem(temp.get(), new Item.Settings().group(ItemGroup.REDSTONE)), Constants.MOD_ID, id);
        return temp;
    }

    private static <T extends Item> Supplier<T> registerItem(Supplier<T> item, String id) {
        return LoaderAgnosticRegistry.registerItem(item, Constants.MOD_ID, id);
    }


    public static void init() {
        STONE_HOPPER_ENTITY = LoaderAgnosticRegistry.registerBlockEntityType(() -> BlockEntityFactory.createBuilder(StoneHopperEntity::new, STONE_HOPPER.get()).build(null), "hplus", "stone_hopper");
        PacketUtils.registerC2SPacket(FilterC2SPacket.class, FilterC2SPacket::new);

        WrenchObservers.addUseOnBlockObserver((context -> {
            BlockState state = context.getWorld().getBlockState(context.getBlockPos());
            HopperWrenchItem.hopperWrenchInteraction(context.getPlayer(),state,context.getWorld(),context.getBlockPos(),true,context.getStack());
        }));
    }

    public static final Supplier<Item> SPEED_UPGRADE = registerItem(() -> new HopperUpgrade() {

        @Override
        public Collection<Item> incompatibleUpgrades() {
            return Set.of(SPEED_UPGRADE_HIGH.get());
        }
    }, "upgrade_speed");


    public static final Supplier<Item> SPEED_UPGRADE_HIGH = registerItem(() -> new HopperUpgrade() {
        @Override
        public Collection<Item> incompatibleUpgrades() {
            return Set.of(SPEED_UPGRADE.get());
        }
    }, "upgrade_speed_high");

    public static final Supplier<Item> REPELLER_UPGRADE = registerItem(() -> new HopperUpgrade() {

        @Override
        public Collection<Item> incompatibleUpgrades() {
            return Set.of(BLACK_HOLE_UPGRADE.get(), SUCTION_UPGRADE.get());
        }
    }, "upgrade_repeller");



    public static final Supplier<Item> SUCTION_UPGRADE = registerItem(() -> new HopperUpgrade() {

        @Override
        public Collection<Item> incompatibleUpgrades() {
            return Set.of(BLACK_HOLE_UPGRADE.get(), REPELLER_UPGRADE.get());
        }
    }, "upgrade_suction");


    public static final Supplier<Item> BLACK_HOLE_UPGRADE = registerItem(() -> new HopperUpgrade() {
        @Override
        public Collection<Item> incompatibleUpgrades() {
            return Set.of(SUCTION_UPGRADE.get(), REPELLER_UPGRADE.get());
        }
    }, "upgrade_black_hole");


    public static final Supplier<ScreenHandlerType<FilterScreenHandler>> FILTER_SCREEN_HANDLER = LoaderAgnosticRegistry.registerSimpleScreen(FilterScreenHandler::new, "hplus", "upgrade_filter");


}
