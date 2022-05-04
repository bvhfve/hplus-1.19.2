package net.flytre.hplus;


import net.flytre.flytre_lib.loader.LoaderCore;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Constants.MOD_ID)
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeMod {

    public ForgeMod() {

        LoaderCore.registerForgeMod("hplus", Registry::init);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientRegistry.init();
        }
    }
}