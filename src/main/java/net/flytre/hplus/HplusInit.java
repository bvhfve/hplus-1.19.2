package net.flytre.hplus;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.flytre.flytre_lib.api.base.util.BakeHelper;
import net.flytre.flytre_lib.api.base.util.PacketUtils;
import net.flytre.hplus.filter.FilterScreenHandler;
import net.flytre.hplus.filter.FilterUpgrade;
import net.flytre.hplus.filter.HopperUpgrade;
import net.flytre.hplus.misc.HopperItem;
import net.flytre.hplus.misc.HopperUpgradeRecipe;
import net.flytre.hplus.misc.StoneHopperEntity;
import net.flytre.hplus.network.FilterC2SPacket;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

public class HplusInit implements ModInitializer {


    public static final Item FILTER_UPGRADE = new FilterUpgrade(new Item.Settings().group(ItemGroup.REDSTONE));
    public static final Item SPEED_UPGRADE = new HopperUpgrade() {

        @Override
        public Collection<Item> incompatibleUpgrades() {
            return Set.of(SPEED_UPGRADE_HIGH);
        }
    };
    public static final Item SPEED_UPGRADE_HIGH = new HopperUpgrade() {
        @Override
        public Collection<Item> incompatibleUpgrades() {
            return Set.of(SPEED_UPGRADE);
        }
    };
    public static final Item REPELLER_UPGRADE = new HopperUpgrade();
    public static final Item VOID_UPGRADE = new HopperUpgrade();
    public static final Item LOCK_UPGRADE = new HopperUpgrade();
    public static final Item BASE_UPGRADE = new Item(new Item.Settings().group(ItemGroup.REDSTONE));


    public static final ScreenHandlerType<FilterScreenHandler> FILTER_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier("hplus", "upgrade_filter"), FilterScreenHandler::new);
    public static SpecialRecipeSerializer<HopperUpgradeRecipe> UPGRADE_RECIPE;
    public static BlockEntityType<StoneHopperEntity> STONE_HOPPER_ENTITY;

    public static final HopperBlock STONE_HOPPER = new HopperBlock(AbstractBlock.Settings.of(Material.STONE).hardness(3.0f)) {
        @Override
        public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
            return new StoneHopperEntity(pos, state);
        }

        @Nullable
        public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
            return world.isClient ? null : checkType(type, STONE_HOPPER_ENTITY, HopperBlockEntity::serverTick);
        }
    };

    @SuppressWarnings("SameParameterValue")
    static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String id, S serializer) {
        return Registry.register(Registry.RECIPE_SERIALIZER, id, serializer);
    }

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier("hplus", "upgrade_base"), BASE_UPGRADE);
        Registry.register(Registry.ITEM, new Identifier("hplus", "upgrade_filter"), FILTER_UPGRADE);


        Registry.register(Registry.ITEM, new Identifier("hplus", "upgrade_repeller"), REPELLER_UPGRADE);
        Registry.register(Registry.ITEM, new Identifier("hplus", "upgrade_speed"), SPEED_UPGRADE);
        Registry.register(Registry.ITEM, new Identifier("hplus", "upgrade_speed_high"), SPEED_UPGRADE_HIGH);
        Registry.register(Registry.ITEM, new Identifier("hplus", "upgrade_void"), VOID_UPGRADE);
        Registry.register(Registry.ITEM, new Identifier("hplus", "upgrade_lock"), LOCK_UPGRADE);

        UPGRADE_RECIPE = register("hplus:upgrade_recipe", new SpecialRecipeSerializer<>(HopperUpgradeRecipe::new));

        Registry.register(Registry.BLOCK, new Identifier("hplus", "stone_hopper"), STONE_HOPPER);
        Registry.register(Registry.ITEM, new Identifier("hplus", "stone_hopper"), new HopperItem(STONE_HOPPER, new Item.Settings().group(ItemGroup.REDSTONE)));
        STONE_HOPPER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "hplus:stone_hopper", FabricBlockEntityTypeBuilder.create(StoneHopperEntity::new, STONE_HOPPER).build(null));


        PacketUtils.registerC2SPacket(FilterC2SPacket.class, FilterC2SPacket::new);

        BakeHelper.fullBake("hplus","hopper",null);
    }
}
