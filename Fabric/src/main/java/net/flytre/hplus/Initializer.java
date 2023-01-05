package net.flytre.hplus;

import net.fabricmc.api.ModInitializer;

public class Initializer implements ModInitializer {
    @Override
    public void onInitialize() {
        Registry.init();
    }
}
