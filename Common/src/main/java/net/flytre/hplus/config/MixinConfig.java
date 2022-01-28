package net.flytre.hplus.config;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import net.flytre.flytre_lib.api.config.ConfigHandler;
import net.flytre.flytre_lib.api.config.ConfigRegistry;
import net.flytre.flytre_lib.api.config.annotation.Description;
import net.flytre.flytre_lib.api.config.annotation.DisplayName;

import java.io.File;

@DisplayName("Hopper Plus - Mixin Config")
public class MixinConfig {


    public static ConfigHandler<MixinConfig> INSTANCE;


    @SerializedName("hopper_minecart_speed")
    @DisplayName("Hopper Minecart Speed")
    @Description("How to modify speed of hopper minecarts. disabled - fixes a compatibility issue with carpet extra and hopper minecarts transfer very fast like vanilla. slow - sync speed with hoppers")
    public HopperMinecartSpeed hopperMinecartSpeed = HopperMinecartSpeed.SLOW;

    public static void initialize() {
        INSTANCE = new ConfigHandler<>(new MixinConfig(), "hplus_mixin", new GsonBuilder().setPrettyPrinting().create());
        INSTANCE.handle(new File("./config/hplus_mixin.json5"));
        ConfigRegistry.registerServerConfig(INSTANCE);
    }


    public enum HopperMinecartSpeed {

        @SerializedName("disabled")
        DISABLED,
        @SerializedName("slow")
        SLOW
    }


}
