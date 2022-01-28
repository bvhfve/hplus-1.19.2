package net.flytre.hplus;


import net.flytre.flytre_lib.api.loader.LoaderCore;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Constants.MOD_ID)
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeMod {

    public ForgeMod() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientRegistry.init();
        }
        LoaderCore.registerForgeMod("hplus", Registry::init);
    }


    @SubscribeEvent
    public static void onLoadComplete(FMLLoadCompleteEvent event) {
    }

    @SubscribeEvent
    public void preInit(FMLCommonSetupEvent event) {
    }
}