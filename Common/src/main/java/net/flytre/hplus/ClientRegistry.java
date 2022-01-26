package net.flytre.hplus;

import net.flytre.flytre_lib.loader.LoaderProperties;
import net.flytre.hplus.filter.FilterScreen;

public class ClientRegistry {

    public static void init() {
        LoaderProperties.register(Registry.FILTER_SCREEN_HANDLER, FilterScreen::new);
    }
}
