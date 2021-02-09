package net.flytre.hplus2.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.hplus2.RegistryHandler;

@Environment(EnvType.CLIENT)
public class Hplus2Client implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RegistryHandler.onInitClient();
    }
}
