package net.flytre.hplus.mixin;

import net.flytre.hplus.config.MixinConfig;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class HplusMixinPlugin implements IMixinConfigPlugin {

    /*
    mixinPackage: the literal mixin package the plugin is specified in, i.e. net.flytre.hplus.mixin
     */
    @Override
    public void onLoad(String mixinPackage) {
        MixinConfig.initialize();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        MixinConfig.HopperMinecartSpeed speed = MixinConfig.INSTANCE.getConfig().hopperMinecartSpeed;

        if (speed != MixinConfig.HopperMinecartSpeed.SLOW && mixinClassName.equals("net.flytre.hplus.mixin.HopperMinecartEntitySpeedMixin"))
            return false;

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
