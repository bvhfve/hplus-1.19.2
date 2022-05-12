package net.flytre.hplus.rei;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import net.flytre.hplus.filter.FilterScreen;

import java.util.List;

public class HplusPlugin implements REIClientPlugin {

    @Override
    public void registerScreens(ScreenRegistry registry) {
        ExclusionZones exclusionZones = registry.exclusionZones();
        exclusionZones.register(FilterScreen.class, screen ->
        {
            int x = screen.getX();
            int y = screen.getY();
            return List.of(new Rectangle(x, y, 176, 170), new Rectangle(x + 176, y, 20, 60));
        });
    }
}
