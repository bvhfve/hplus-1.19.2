package net.flytre.hplus;

import net.flytre.flytre_lib.loader.LoaderProperties;
import net.flytre.flytre_lib.loader.registry.BlockEntityRegisterer;
import net.flytre.flytre_lib.loader.registry.ScreenHandlerRegisterer;
import net.flytre.hplus.filter.FilterScreenHandler;
import net.flytre.hplus.filter.FilterUpgrade;
import net.flytre.hplus.filter.HopperUpgrade;
import net.flytre.hplus.misc.HopperUpgradeRecipe;
import net.flytre.hplus.misc.StoneHopperEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

public class Registry {

    public static final Item FILTER_UPGRADE = register(new FilterUpgrade(new Item.Settings().group(ItemGroup.REDSTONE)), "upgrade_filter");
    public static final Item REPELLER_UPGRADE = register(new HopperUpgrade(), "upgrade_repeller");
    public static final Item VOID_UPGRADE = register(new HopperUpgrade(), "upgrade_void");
    public static final Item LOCK_UPGRADE = register(new HopperUpgrade(), "upgrade_lock");
    public static final Item BASE_UPGRADE = register(new Item(new Item.Settings().group(ItemGroup.REDSTONE)), "upgrade_base");
    public static SpecialRecipeSerializer<HopperUpgradeRecipe> UPGRADE_RECIPE = LoaderProperties.register(new SpecialRecipeSerializer<>(HopperUpgradeRecipe::new), "hplus", "upgrade_recipe");
    public static BlockEntityType<StoneHopperEntity> STONE_HOPPER_ENTITY;
    public static final HopperBlock STONE_HOPPER = register(new HopperBlock(AbstractBlock.Settings.of(Material.STONE).hardness(3.0f)) {
        @Override
        public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
            return new StoneHopperEntity(pos, state);
        }

        @Nullable
        public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
            return world.isClient ? null : checkType(type, STONE_HOPPER_ENTITY, HopperBlockEntity::serverTick);
        }
    }, "stone_hopper");    public static final Item SPEED_UPGRADE = register(new HopperUpgrade() {

        @Override
        public Collection<Item> incompatibleUpgrades() {
            return Set.of(SPEED_UPGRADE_HIGH);
        }
    }, "upgrade_speed");

    static {
        STONE_HOPPER_ENTITY = BlockEntityRegisterer.createBuilder(StoneHopperEntity::new, STONE_HOPPER).build(null);
    }

    @SuppressWarnings("SameParameterValue")
    static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String id, S serializer) {
        return net.minecraft.util.registry.Registry.register(net.minecraft.util.registry.Registry.RECIPE_SERIALIZER, id, serializer);
    }

    public static <T extends Block> T register(T block, String id) {
        var tmp = LoaderProperties.register(block, Constants.MOD_ID, id);
        LoaderProperties.register(new BlockItem(block, new Item.Settings().group(ItemGroup.REDSTONE)), Constants.MOD_ID, id);
        return tmp;
    }

    private static <T extends Item> T register(T item, String id) {
        return LoaderProperties.register(item, Constants.MOD_ID, id);
    }

    public static void init() {
        //$$REGISTRY
    }



    public static final Item SPEED_UPGRADE_HIGH = register(new HopperUpgrade() {
        @Override
        public Collection<Item> incompatibleUpgrades() {
            return Set.of(SPEED_UPGRADE);
        }
    }, "upgrade_speed_high");


    public static final ScreenHandlerType<FilterScreenHandler> FILTER_SCREEN_HANDLER = LoaderProperties.register((ScreenHandlerRegisterer.SimpleFactory<FilterScreenHandler>) FilterScreenHandler::new, "hplus", "upgrade_filter");


}
