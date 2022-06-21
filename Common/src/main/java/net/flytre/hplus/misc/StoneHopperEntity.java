package net.flytre.hplus.misc;

import net.flytre.hplus.Registry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class StoneHopperEntity extends HopperBlockEntity {

    public StoneHopperEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public BlockEntityType<?> getType() {
        return Registry.STONE_HOPPER_ENTITY.get();
    }


    @Override
    public Text getContainerName() {
        return Text.translatable("container.stone_hopper");
    }
}
