package net.flytre.hplus;

import net.flytre.flytre_lib.loader.LoaderAgnosticClientRegistry;
import net.flytre.hplus.filter.FilterScreen;

public class ClientRegistry {

    public static void init() {
        LoaderAgnosticClientRegistry.register(Registry.FILTER_SCREEN_HANDLER::get, FilterScreen::new);
    }
}
