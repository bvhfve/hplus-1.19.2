package net.flytre.hplus.mixin;

import net.flytre.hplus.misc.MixinHelper;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Sync hopper minecart speed with hopper speed; Toggled in mixin config
 */
@Mixin(HopperMinecartEntity.class)
public class HopperMinecartEntitySpeedMixin {

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 4))
    private int hplus$insertAndExtractCooldown(int i) {
        return MixinHelper.getMaxCooldown(this);
    }
}
