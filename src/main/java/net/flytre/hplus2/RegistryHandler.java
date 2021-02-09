package net.flytre.hplus2;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.flytre.hplus2.filter.FilterScreen;
import net.flytre.hplus2.filter.FilterScreenHandler;
import net.flytre.hplus2.filter.FilterUpgrade;
import net.flytre.hplus2.filter.ItemUpgrade;
import net.flytre.hplus2.recipe.HopperUpgradeRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RegistryHandler {


    public static final Item FILTER_UPGRADE = new FilterUpgrade(new FabricItemSettings().group(ItemGroup.REDSTONE));
    public static final Item SPEED_UPGRADE = new ItemUpgrade();
    public static final Item SPEED_UPGRADE_HIGH = new ItemUpgrade();
    public static final Item VACUUM_UPGRADE = new ItemUpgrade();
    public static final Item VOID_UPGRADE = new ItemUpgrade();
    public static final Item LOCK_UPGRADE = new ItemUpgrade();
    public static final Item BASE_UPGRADE = new Item(new FabricItemSettings().group(ItemGroup.REDSTONE));


    public static final ScreenHandlerType<FilterScreenHandler> FILTER_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier("hplus", "upgrade_filter"), FilterScreenHandler::new);
    public static final Identifier FILTER_MODE = new Identifier("hplus", "filter_mode");
    public static final Identifier FILTER_TRASH = new Identifier("hplus", "filter_trash");
    public static SpecialRecipeSerializer<HopperUpgradeRecipe> UPGRADE_RECIPE;


    public static void onInit() {

        Registry.register(Registry.ITEM, new Identifier("hplus", "upgrade_base"), BASE_UPGRADE);
        Registry.register(Registry.ITEM, new Identifier("hplus", "upgrade_filter"), FILTER_UPGRADE);


        Registry.register(Registry.ITEM, new Identifier("hplus", "upgrade_vacuum"), VACUUM_UPGRADE);
        Registry.register(Registry.ITEM, new Identifier("hplus", "upgrade_speed"), SPEED_UPGRADE);
        Registry.register(Registry.ITEM, new Identifier("hplus", "upgrade_speed_high"), SPEED_UPGRADE_HIGH);
        Registry.register(Registry.ITEM, new Identifier("hplus", "upgrade_void"), VOID_UPGRADE);
        Registry.register(Registry.ITEM, new Identifier("hplus", "upgrade_lock"), LOCK_UPGRADE);


        ServerPlayNetworking.registerGlobalReceiver(FILTER_MODE, (server, player, handler, buf, responseSender) -> {
            int state = buf.readInt();
            server.execute(() -> {
                ItemStack stack = player.getMainHandStack();
                stack.getOrCreateSubTag("filter").putInt("type", state);
            });
        });


        UPGRADE_RECIPE = register("hplus:upgrade_recipe", new SpecialRecipeSerializer<>(HopperUpgradeRecipe::new));


    }

    @Environment(EnvType.CLIENT)
    public static void onInitClient() {
        ScreenRegistry.register(FILTER_SCREEN_HANDLER, FilterScreen::new);
    }


    static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String id, S serializer) {
        return Registry.register(Registry.RECIPE_SERIALIZER, id, serializer);
    }

}
