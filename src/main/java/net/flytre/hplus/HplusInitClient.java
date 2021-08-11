package net.flytre.hplus;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.flytre.hplus.filter.FilterScreen;

public class HplusInitClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        ScreenRegistry.register(HplusInit.FILTER_SCREEN_HANDLER, FilterScreen::new);

    }
}
